# Contratos da API REST

Base: `/api/v1`. JSON UTF-8. Autenticação por `Authorization: Bearer <token>` (JWT HS256, validade `JWT_TTL_MINUTES`, padrão 12 h). Datas em ISO-8601 UTC; valores monetários em reais com 2 casas.

## Erros
Formato único: `{"codigo": "...", "mensagem": "...", "campos": {"campo": "mensagem"}, "momento": "..."}`.

| HTTP | Quando | Exemplos de `codigo` |
|---|---|---|
| 400 | Validação de entrada | `DADOS_INVALIDOS` (com `campos`), `REQUISICAO_INVALIDA` |
| 401 | Sem token/expirado, credenciais | `NAO_AUTENTICADO`, `CREDENCIAIS_INVALIDAS`, `NOTIFICACAO_INVALIDA` |
| 403 | Papel sem permissão | `ACESSO_NEGADO` |
| 404 | Recurso inexistente ou de outro usuário | `NAO_ENCONTRADO` |
| 409 | Conflito de estado | `MAQUINA_INDISPONIVEL`, `MAQUINA_EM_MANUTENCAO`, `PEDIDO_EXPIRADO`, `EMAIL_EM_USO`, `SEM_SECADORA_DISPONIVEL` |
| 422 | Regra de negócio | `SERVICO_INCOMPATIVEL`, `CODIGO_INVALIDO`, `VALOR_DIVERGENTE`, `SENHA_ATUAL_INCORRETA` |
| 423 | Conta bloqueada (5 falhas, 15 min) | `CONTA_BLOQUEADA` |
| 503 | Integração indisponível | `GATEWAY_INDISPONIVEL`, `RECUPERACAO_INDISPONIVEL`, `GOOGLE_DESATIVADO`, `FALHA_SECADORA` |

## Endpoints
| Método e rota | Acesso | Descrição |
|---|---|---|
| `GET /health` | público | Saúde técnica |
| `POST /auth/register` | público | `{nome, email?, telefone?, senha}` → sessão. Senha 8–72 com letras e números |
| `POST /auth/login` | público | `{identificador, senha}` (e-mail ou telefone) → sessão. Só clientes |
| `POST /auth/google` | público | `{idToken}` → sessão (RF03) |
| `POST /auth/password-recovery` | público | `{email}` → 202 sempre (não revela cadastro) |
| `POST /auth/password-reset` | público | `{email, codigo, novaSenha}` |
| `POST /admin/auth/login` | público | `{identificador, senha}` → sessão. Só administradores |
| `GET/PUT /me`, `PUT /me/password` | cliente | Perfil e troca de senha |
| `GET /machines?filtro=LAVAR\|SECAR\|AMBOS` | autenticado | Lista com `status`, `minutosRestantes`, `reservada`, `contratavel` |
| `GET /machines/{id}` | autenticado | Detalhe + serviços aplicáveis ao tipo |
| `GET /services` | autenticado | Serviços e preços |
| `POST /orders` | cliente | `{maquinaId, servico}` → pedido `AGUARDANDO_PAGAMENTO` com `expiraEm` (+5 min) |
| `GET /orders?pagina&tamanho` | cliente | Histórico paginado |
| `GET /orders/{id}` | dono | Pedido, pagamento atual, ciclos, `segundosParaExpirar`, `podeIniciarSecagem` |
| `POST /orders/{id}/cancel` | dono | Libera a reserva antes do pagamento |
| `POST /orders/{id}/checkout` | dono | `{metodo: PIX\|CARTAO_CREDITO\|CARTAO_DEBITO}` → cobrança (`codigoPix`, `checkoutUrl`). Idempotente por método |
| `POST /orders/{id}/drying` | dono | Combo: inicia a secagem em uma secadora livre |
| `GET /wallet`, `DELETE /wallet/methods/{id}` | cliente | Pagamentos e cartões tokenizados |
| `POST /payments/webhook/{provedor}` | gateway | Notificação autenticada pelo adaptador do provedor ativo |
| `GET /admin/reports?periodo=MES\|ANO&ano&mes` | admin | Ganhos, variação (nula sem base), série, totais, `atualizadoEm` |
| `GET/POST /admin/machines`, `PUT /admin/machines/{id}`, `PATCH /admin/machines/{id}/status` | admin | Gestão de máquinas (status só `DISPONIVEL`/`MANUTENCAO`) |
| `GET /admin/services`, `PUT /admin/services/{id}` | admin | Preço, descrição e duração |
| `GET/POST /sandbox/checkout/{transacao}` | público, **só com `PAYMENT_PROVIDER=sandbox`** | Página de homologação para aprovar/recusar |
| `PUT /admin/simulator/machines/{id}/failure?ativa=` | admin, **só com `MACHINE_CONTROLLER=simulador`** | Força falha de início (CT15) |

## Integração de pagamento
Sequência implementada: app cria pedido → backend reserva sob trava → `POST checkout` cria cobrança via `PaymentGateway` → app exibe PIX ou abre `checkoutUrl` em Custom Tab → **somente** a notificação autenticada (`interpretarNotificacao`) aprova → evento gravado em `eventos_pagamento` (deduplicação) → valor conferido → pedido `PAGO` → após o commit, comando à máquina → `EM_ANDAMENTO`. O retorno do checkout ao app é só UX; o app consulta o pedido a cada 3 s.

Casos tratados: recusa (pedido segue aguardando; outro método pode ser usado), reentrega do mesmo evento, segundo pagamento aprovado do mesmo pedido, pagamento após expiração/cancelamento (estorno automático), divergência de valor (rejeitado, sem aprovação).

**Bex — Pendente:** `BexPaymentGateway` não presume endpoints nem formatos; responde como indisponível até a documentação oficial. Ao implementar, mapear o webhook/consulta para `NotificacaoPagamento` e a tokenização para `metodos_pagamento`.

## Máquina
`MachineController.iniciarCiclo(maquina, duracaoMin, chaveIdempotencia)`. O comando confirmado é gravado com chave única `pedido:{id}:{etapa}`, impedindo início duplicado. Falha → comando `FALHOU` registrado e estorno integral (primeira etapa). **Pendente:** protocolo real e sinal de término vindo do equipamento.
