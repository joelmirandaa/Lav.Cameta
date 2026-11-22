# lav.cametá

Sistema da lavanderia de autoatendimento **lav.cametá** (Cametá – PA): aplicativo Android nativo para clientes e administração, API REST em Spring Boot e banco MySQL.

**Estado (2026-11-22):** fluxos de cliente e administrador implementados ponta a ponta e testados contra MySQL real. As integrações externas **Bex** (pagamento) e **hardware das máquinas** continuam sem contrato oficial; em seu lugar existem um *gateway sandbox* e um *simulador de máquinas* para homologação, ativados somente por configuração. Veja [Pendências](#pendências).

## Fontes e classificação
- `fontes/Documentacao-Lav-Cameta.pdf` (v1.0): requisitos RF01–RF21, RNF01–RNF10, RN01–RN13, telas T01–T07, ER e plano de testes.
- `fontes/Prompt-original.txt`: estrutura, linguagem e decisão pelo checkout Bex.
- `lav-cameta-guia-de-design.md`: identidade visual oficial (paleta azul-marinho/azul-água, Outfit, Lucide). Substitui a referência roxa do protótipo WashFlow (RNF09).
- **Confirmado** = expresso na documentação; **Proposto** = decisão técnica; **Pendente** = depende de informação externa.

## O que está implementado
| Módulo | Requisitos | Situação |
|---|---|---|
| Cadastro e login por e-mail/telefone, bloqueio após 5 falhas por 15 min | RF01, RF02, UC02, CT01–CT03 | Concluído |
| Login com Google (ID token validado no backend) | RF03 | Concluído; exige `GOOGLE_CLIENT_ID` e Client ID no app. Sem configuração o botão não aparece |
| Recuperação de senha por código enviado por e-mail | RF04, CT05 | Concluído; exige SMTP (`MAIL_*`) |
| Lista de máquinas com status em tempo real (atualização a cada 5 s), filtros Lavar/Secar/Ambos | RF05, RF06, RNF02 | Concluído |
| Detalhe da máquina, escolha do serviço com preço | RF07–RF09 | Concluído |
| Pedido com reserva atômica de 5 min, expiração automática | RN02, RN03, RN05, RN07, CT08, CT09, CT13, CT16 | Concluído |
| Checkout PIX (QR Code + copia e cola) e cartão (checkout externo) | RF10, RF11 | Fluxo concluído; **provedor real Bex pendente** (sandbox disponível) |
| Confirmação de pagamento somente pelo backend, idempotente, com validação de valor | RF12, RN04, RN13 | Concluído |
| Início automático da máquina, acompanhamento, fim de ciclo, notificação local | RF13, UC08 | Concluído; **protocolo de hardware pendente** (simulador disponível) |
| Estorno integral se a máquina não iniciar, ou se o pagamento chegar após a expiração | RN06, CT15 | Concluído (com reprocessamento automático) |
| Combo Lavar + Secar (lavadora e depois secadora) | RN08, CT11 | Concluído (ver [decisão AD-010](arquitetura/decisoes.md)) |
| Histórico, carteira, perfil e troca de senha | RF14–RF16 | Concluído (carteira só lista cartões tokenizados pelo gateway) |
| Acesso administrativo separado, relatórios Mês/Ano com gráfico, variação, resumo, data de atualização, escolha de período | RF17–RF21, RN09–RN11 | Concluído |
| Gestão de máquinas (cadastro, edição, manutenção) e de preços/durações | UC15, RN01 | Concluído |

## Arquitetura
- **Android** (`android/`): Java 17, MVVM — Activity/Fragment → ViewModel → Repository → Retrofit. Sem acesso direto ao banco. Tema e tokens do guia de design em `res/values`. Detalhes em [arquitetura](arquitetura/decisoes.md).
- **Backend** (`backend/`): Java 21, Spring Boot 4.1.2, camadas controller → service → repository → entity, DTOs, Spring Security com JWT (HS256), Flyway, integrações isoladas por portas (`PaymentGateway`, `MachineController`).
- **Banco**: MySQL 8.4; migrations em `backend/src/main/resources/db/migration` (única fonte executável). Modelo em [banco de dados](banco-de-dados/modelagem.md).
- Contratos REST em [API](api/contratos.md).

## Requisitos de ambiente
| Ferramenta | Versão usada | Observação |
|---|---|---|
| JDK | 21 (backend) e 17+ (Gradle/Android) | O wrapper Gradle 8.13 **não** roda com JDK 25/26; use `JAVA_HOME` apontando para o JDK 21 |
| Maven | via `./mvnw` (Maven 3.9.11) | Não precisa instalar |
| Gradle | via `./gradlew` (8.13) | Não precisa instalar |
| Android SDK | Platform 35, Build-Tools 35 | O Gradle baixa automaticamente se as licenças estiverem aceitas |
| Docker | qualquer recente | Para o MySQL local e para os testes (Testcontainers) |

Itens baixados neste ambiente estão listados em [utilitarios.md](utilitarios.md). **Passo a passo para rodar e testar: [rodar.md](rodar.md).**

## Configurar o banco de dados
**Opção A — Docker (recomendado para desenvolvimento):**
```bash
cp backend/.env.example backend/.env        # preencha DB_PASSWORD, JWT_SECRET etc.
docker compose --env-file backend/.env up -d mysql
```
Sobe MySQL 8.4 em `127.0.0.1:3307`, com o schema `lavicameta` e o usuário da aplicação.

**Opção B — MySQL existente:** edite a senha em `database/scripts/criar-banco-e-usuario.sql` e execute-o como administrador. Ajuste `DB_URL`.

As tabelas e os preços padrão (RN01) são criados pelo **Flyway** ao iniciar o backend (`V1`–`V3`). Não há dados fictícios: máquinas e administrador são cadastrados por você.

## Executar o backend
```bash
cd backend
set -a; source .env; set +a                 # carrega as variáveis do arquivo .env
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64   # ajuste ao seu sistema
./mvnw spring-boot:run
curl http://localhost:8080/api/v1/health    # {"status":"UP"}
```
Variáveis obrigatórias: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (≥ 32 bytes). Na primeira execução defina `ADMIN_BOOTSTRAP_EMAIL` e `ADMIN_BOOTSTRAP_PASSWORD` para criar o administrador (não existe senha padrão). Todas as variáveis estão comentadas em `backend/.env.example`.

**Modos de integração**
| Variável | Produção | Homologação local |
|---|---|---|
| `PAYMENT_PROVIDER` | `bex` (padrão) — hoje responde 503 “pagamento indisponível” até a integração ser escrita | `sandbox` — gera cobranças de teste; aprovar/recusar em `GET /api/v1/sandbox/checkout/{transacao}` ou por webhook assinado (HMAC-SHA256 em `X-Sandbox-Signature` com `PAYMENT_WEBHOOK_SECRET`) |
| `MACHINE_CONTROLLER` | `hardware` (padrão) — sem protocolo, todo início falha e gera estorno | `simulador` — aceita o comando; o ciclo termina pela duração do serviço. `PUT /api/v1/admin/simulator/machines/{id}/failure?ativa=true` força falha (CT15) |

No sandbox, o app abre a página de homologação ao tocar no QR Code ou em “Abrir pagamento seguro”; nenhuma cobrança real é feita.

## Executar o app Android
1. Abra `android/` no Android Studio (ou use a linha de comando).
2. A URL da API vem de propriedades Gradle (em `~/.gradle/gradle.properties` ou `-P`):
   - `lavicameta.apiBaseUrl.debug` — padrão `http://10.0.2.2:8080/api/v1/` (emulador → máquina local). Para aparelho físico use o IP da máquina na rede.
   - `lavicameta.apiBaseUrl.release` — **obrigatório HTTPS** (RNF05); o build release bloqueia HTTP.
   - `lavicameta.googleWebClientId` — opcional, habilita “Entrar com Google”.
3. Compilar e instalar:
```bash
cd android
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew assembleDebug          # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # com emulador/aparelho conectado
```
O acesso administrativo fica no link “Acesso administrativo” da tela de login.

## Testes
```bash
cd backend && ./mvnw test                  # 23 testes (unitários + integração com MySQL 8.4 via Testcontainers; requer Docker)
cd android && ./gradlew testDebugUnitTest  # 9 testes unitários
```
Resultados e cobertura dos casos CT01–CT25 em [plano de testes](requisitos/testes.md).

## Pendências
- **Bex:** documentação oficial (endpoints, autenticação, webhook/assinatura, estorno, tokenização de cartão). Implementar em `integration/bex/BexPaymentGateway` mantendo a interface `PaymentGateway`.
- **Máquinas:** fabricante, protocolo, conectividade e telemetria de término. Implementar em `integration/machine/HardwareMachineController`; hoje o fim do ciclo é calculado pela duração configurada.
- **Durações dos serviços:** o PDF não informa; valores iniciais 40/40/80 min devem ser confirmados (editáveis em Preços).
- **SMS** para recuperação de quem cadastrou só telefone; **push (FCM)** para avisar fim de ciclo com o app fechado (hoje a notificação é local, enquanto o acompanhamento está aberto).
- **Infra de produção:** TLS no proxy reverso, segredos em cofre, backups, monitoramento (RNF08) e teste de carga CT25.
- Política de retenção/anonimização de dados pessoais (LGPD, RNF07).

## Documentação
[Como rodar e testar](rodar.md) · [Arquitetura](arquitetura/decisoes.md) · [API](api/contratos.md) · [Modelo de dados](banco-de-dados/modelagem.md) · [Módulos](funcionalidades/modulos.md) · [Fluxos](fluxos/jornadas.md) · [Requisitos](requisitos/requisitos.md) · [Regras](requisitos/regras-negocio.md) · [Testes](requisitos/testes.md) · [Estilo](STYLE.md) · [Guia de design](lav-cameta-guia-de-design.md) · [Agentes](AGENTS.md) · [Mudanças](alteracoes/registro.md) · [Validação](alteracoes/validacao.md) · [Utilitários baixados](utilitarios.md)
