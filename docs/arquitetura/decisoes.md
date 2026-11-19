# Arquitetura e decisões

## AD-001 — Monorepo separado (Proposto)
Android Java e backend Java 21 no mesmo repositório, com builds isolados. APIs REST JSON. Banco acessado só pela API.

## AD-002 — Arquitetura Android (Proposto)
UI `Activity/Fragment` → ViewModel → UseCase (quando necessário) → Repository → Remote API/Local cache. Estados de rede e erro explícitos. Navegação conforme protótipo após homologação.

## AD-003 — Backend (Proposto)
Controllers → Services transacionais → Repositories JPA → MySQL. DTOs limitam dados expostos; integração Bex e adaptador de máquina isolados. Aplicar RBAC e idempotência antes dos fluxos financeiros.

## AD-004 — Migrações (Proposto)
Flyway em `backend/src/main/resources/db/migration` como fonte única. `database/scripts` abriga comandos auxiliares. O primeiro arquivo é apenas um marcador estrutural SQL válido; **não representa modelagem de domínio concluída**.

## AD-005 — Eventos críticos (Pendente integração)
Webhook assinado/autenticado ou consulta comprovada ao gateway → validação de valor e pedido → transição idempotente → fila de comando com rastreio → confirmação do equipamento → ciclo. Contratos, retry e tratamento de falhas dependem de Bex/hardware.

## AD-006 — Segurança (Proposto)
Spring Security, hash robusto, autorização no servidor, TLS, princípio do menor privilégio, gestão de segredos e auditoria. JWT/sessão ainda dependem de decisão formal.

## Decisões abertas
Documento de origem traz roxo WashFlow; nome do projeto final Lavicameta, estilo definitivo pendente. Combo exige duas máquinas. Gateway Bex consta do prompt, mas especificação do provedor não foi anexada. Hardware/firmware e rede ainda desconhecidos.
