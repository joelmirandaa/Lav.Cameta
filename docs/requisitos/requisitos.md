# Requisitos funcionais — rastreabilidade

**Origem:** PDF seção 1.4, páginas 1–2. IDs `RF001`–`RF021` normalizados; ID original preservado. Todos classificados **Confirmado** enquanto intenção do PDF, não como funcionalidade implementada. Dependências são **propostas técnicas**. Critérios de aceitação resumem intenção verificável; detalhar na implementação.

| ID | Original | Descrição | Prioridade | Dependência proposta | Aceitação resumida |
|---|---|---|---|---|---|
| RF001 | RF01 | Cadastro nome, e-mail/telefone e senha | Alta | API e usuários | Cadastro válido persistido |
| RF002 | RF02 | Login com e-mail/telefone e senha | Alta | Cadastro e autenticação | Credencial válida abre catálogo |
| RF003 | RF03 | Login Google | Média | OAuth/Google | Conta Google autenticada |
| RF004 | RF04 | Recuperar senha | Alta | Canal de recuperação | Token seguro redefiniu senha |
| RF005 | RF05 | Listar máquinas e estados | Alta | Telemetria | Exibe disponíveis, em uso/minutos e manutenção |
| RF006 | RF06 | Filtrar Lavar/Secar/Ambos | Média | Catálogo | Filtro correto |
| RF007 | RF07 | Detalhes de máquina | Média | Catálogo | Exibe status/capacidade/recursos |
| RF008 | RF08 | Escolher serviço | Alta | Máquinas/preços | Cria pedido coerente |
| RF009 | RF09 | Mostrar preço antes de pagar | Alta | Serviços | Preço visível e fixado |
| RF010 | RF10 | PIX QR/copia e cola | Alta | Bex | Dados de pagamento apresentados |
| RF011 | RF11 | Cartão crédito/débito | Média | Bex | Checkout seguro disponível |
| RF012 | RF12 | Confirmar pagamento automático | Alta | Gateway | Aprovação validada no backend |
| RF013 | RF13 | Iniciar serviço automaticamente | Alta | Gateway/hardware | Início apenas após aprovação |
| RF014 | RF14 | Histórico de serviços | Média | Pedidos | Lista serviços do cliente |
| RF015 | RF15 | Carteira e métodos salvos | Baixa | Tokenização | Exibe meios sem PAN |
| RF016 | RF16 | Editar perfil | Baixa | Usuários | Dados autorizados atualizados |
| RF017 | RF17 | Login admin restrito | Alta | RBAC | Cliente não entra no admin |
| RF018 | RF18 | Relatório mensal | Alta | Pagamentos | Ganho/variação/gráfico semanal |
| RF019 | RF19 | Relatório anual | Alta | Pagamentos | Ganho/variação/gráfico mensal |
| RF020 | RF20 | Resumo período e pagamentos | Alta | Relatórios | Totais agregados corretos |
| RF021 | RF21 | Última atualização | Média | Relatórios | Timestamp explícito |

## Requisitos não funcionais
**Origem PDF seção 1.5, páginas 2–3; todos Confirmado.**

| ID | Original | Requisito | Verificação planejada | Situação (2026-11-27) | Evidência / o que falta |
|---|---|---|---|---|---|
| RNF001 | RNF01 | Android nativo | Build/emulador Android | ✓ Atendido | App Java/MVVM em `android/`; `./gradlew assembleDebug`; validado em emulador e celular físico |
| RNF002 | RNF02 | Status em até 5 segundos | Teste de propagação | ✓ Atendido | Polling de 5 s na lista e no acompanhamento (`MachinesViewModel`, `CycleViewModel`, `Poller`) |
| RNF003 | RNF03 | PIX em até 10 s após compensação | Sandbox com timestamps | ◐ Parcial | Backend processa o webhook na hora e o app consulta o pedido a cada 3 s; medição real depende do provedor Bex (pendente) |
| RNF004 | RNF04 | Hash seguro de senhas | Revisão de segurança | ✓ Atendido | BCrypt via `DelegatingPasswordEncoder` (AD-006) |
| RNF005 | RNF05 | HTTPS/TLS | Inspeção de rede | ◐ Parcial | Build release bloqueia HTTP (`network_security_release.xml`); falta certificado TLS no proxy reverso de produção |
| RNF006 | RNF06 | Sem PAN; tokenização | Inspeção de banco e logs | ✓ Atendido | `MetodoPagamentoSalvo` guarda só token do gateway, bandeira e últimos 4 dígitos; teste CT24. Geração do token depende da Bex |
| RNF007 | RNF07 | LGPD | Avaliação privacidade | ◐ Parcial | Dados mínimos, senhas com hash, DTOs sem dados sensíveis; falta política de retenção/anonimização |
| RNF008 | RNF08 | 99% de disponibilidade | Observabilidade/SLO | ◐ Parcial | Health check em `/api/v1/health`; falta infraestrutura de produção (monitoramento, backup) e teste de carga CT25 |
| RNF009 | RNF09 | Português e identidade roxa no protótipo | Revisão de UI | ✓ Atendido | Textos em pt-BR e identidade roxa do guia de design (2026-11-25) |
| RNF010 | RNF10 | Admin separado do cliente | Testes RBAC | ✓ Atendido | Papéis CLIENTE/ADMIN no JWT, `/admin/**` só ADMIN, tela própria `AdminLoginActivity`; testes de integração de acesso indevido |

**Resumo:** 6 atendidos (RNF01, 02, 04, 06, 09, 10) e 4 parciais (RNF03, 05, 07, 08). Os parciais têm a parte de código pronta e dependem de fornecedor (Bex), infraestrutura de produção ou definição de política.

## Escopo de MVP — proposta
**MVP:** RF001, RF002, RF004, RF005, RF008–RF010, RF012, RF013, RF017–RF020; incluir filtros e detalhe por dependência de navegação. **Posteriores:** login Google, cartão, histórico, acompanhamento robusto, perfil e gestão completa. **Futuro:** carteira de métodos salvos. Prioridades do PDF permanecem autoridade; recorte MVP precisa de aprovação.
