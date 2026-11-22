# Arquitetura e decisões

## AD-001 — Monorepo separado (Implementado)
Android Java e backend Java 21 no mesmo repositório, com builds isolados (`android/` Gradle, `backend/` Maven). APIs REST JSON. Banco acessado só pela API.

## AD-002 — Arquitetura Android (Implementado)
`ui/activity`, `ui/fragment` (telas, sem regra de negócio) → `viewmodel` (estado em `LiveData<Result<T>>`, validação de formulário, polling) → `data/repository` (Retrofit, sessão) → `data/remote` (`ApiService`, `ApiCallback` que traduz o formato de erro da API). `domain/model` são os modelos de resposta; `domain/usecase/FormValidator` espelha as regras de validação do backend. Injeção manual em `AppContainer` + `ViewModelFactory` (sem framework de DI — KISS).
- Sessão: token JWT em `SharedPreferences` privadas; 401 em rota autenticada encerra a sessão e volta ao login.
- Tempo real (RNF02): polling de 5 s na lista e no acompanhamento, 3 s no pagamento, apenas com a tela visível.
- Responsividade: grid 1/2/3 colunas (`values`, `values-sw600dp`, `values-sw900dp`), conteúdo com largura máxima (`MaxWidthLinearLayout`) e margens maiores em tablet; edge-to-edge tratado em `BaseActivity`.
- Bibliotecas (justificadas): Material Components (componentes do guia), Retrofit/OkHttp/Gson (API), ZXing core (QR Code PIX local), AndroidX Browser (checkout em Custom Tab), Credential Manager + googleid (RF03). Gráficos desenhados em `ChartView` para evitar dependência externa.

## AD-003 — Backend (Implementado)
Controllers (validação `@Valid`, sem regra) → Services transacionais → Repositories Spring Data JPA → MySQL. DTOs como `record`; `DtoMapper` impede expor hash de senha ou tokens. `GlobalExceptionHandler` centraliza erros. `Clock` injetado para testes de tempo.

## AD-004 — Migrações (Implementado)
Flyway em `backend/src/main/resources/db/migration` é a fonte única; `ddl-auto: validate`. `database/scripts` contém apenas SQL de apoio.

## AD-005 — Eventos críticos (Implementado com adaptadores de homologação)
Notificação autenticada pelo adaptador → deduplicação por `(provedor, evento_id)` → conferência de valor → transição idempotente sob trava do pedido → evento `AFTER_COMMIT` → comando à máquina com chave de idempotência → ciclo. Falhas são retomadas pelo agendador (`TarefasAgendadas`, a cada 5 s): pedidos pagos sem ciclo, estornos pendentes, reservas vencidas e ciclos terminados.

## AD-006 — Segurança (Implementado)
Spring Security stateless; JWT HS256 assinado com `JWT_SECRET` (≥ 32 bytes) e papel `CLIENTE`/`ADMIN`; rotas `/admin/**` só ADMIN e rotas do cliente só CLIENTE (RNF10/RN11). Senhas com BCrypt (`DelegatingPasswordEncoder`). Bloqueio após 5 falhas por 15 min. Mensagens de login e recuperação não revelam se a conta existe. Primeiro admin via variáveis de ambiente, sem senha padrão. TLS deve terminar em proxy reverso em produção; o app release só aceita HTTPS.
- **Decisão (antes pendente):** JWT sem refresh token, validade padrão de 12 h. Revogação imediata não é suportada — Pendente avaliar se necessário.

## AD-007 — Integrações por portas e adaptadores de homologação (Implementado)
`PaymentGateway` (Bex = padrão, falha explicitamente até haver documentação; `sandbox` para homologação) e `MachineController` (hardware = padrão, falha explicitamente; `simulador` para homologação). Seleção por variável de ambiente; os adaptadores de teste registram aviso no log ao iniciar. Justificativa: o PDF prevê “gateway em modo sandbox e máquina simulada” no ambiente de testes (10.2) e “criar simulador” como mitigação de risco; AGENTS.md proíbe simular sucesso em produção — por isso o padrão é sempre o adaptador real.

## AD-008 — Concorrência (Implementado)
Reserva com `SELECT … FOR UPDATE` na máquina e isolamento `READ COMMITTED` no pool (no `REPEATABLE READ` padrão do MySQL, a verificação após a trava lia um snapshot antigo e permitia reservas duplicadas — detectado pelo teste CT16). RN13 também garantida por índice único no banco.

## AD-009 — Recuperação de senha (Implementado)
Código alfanumérico de 8 caracteres por e-mail (SMTP configurável), hash SHA-256, 30 min, 5 tentativas, reenvio limitado a 1/min. Escolhido em vez de link para não exigir deep link/domínio verificado. Sem SMTP configurado a API responde 503 de forma explícita.

## AD-010 — Combo Lavar + Secar (Implementado; Proposto para validação do negócio)
O pagamento único (R$ 18) reserva a lavadora. Ao fim da lavagem o cliente toca “Iniciar secagem” e o sistema escolhe uma secadora livre (as roupas precisam ser transferidas manualmente, por isso a ação é do cliente). Sem secadora livre, o app pede para tentar em instantes. Falha ao iniciar a secadora não gera estorno automático (parte do serviço foi prestada) — fica registrada para o atendimento. Não se aceita combo se nenhuma secadora estiver em operação.

## AD-011 — Identidade visual (Implementado)
`lav-cameta-guia-de-design.md` é a especificação oficial; tokens em `res/values/colors.xml`, `dimens.xml`, `themes.xml`, `styles.xml`; fonte Outfit embarcada; ícones Lucide como vetores; logos recortados dos arquivos oficiais em `assets/img` sem alteração do desenho.

## Decisões abertas
Contrato Bex; protocolo e sinal de término das máquinas; durações reais dos ciclos; push (FCM) e SMS; revogação de tokens; política LGPD de retenção.
