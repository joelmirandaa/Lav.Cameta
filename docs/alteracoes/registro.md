# Registro de decisões

2026-11-18 — Estrutura inicial criada a partir dos dois anexos. Sem funcionalidades de domínio. Android Java MVVM; backend Java 21/Spring Boot 4.1.2; MySQL/Flyway; integração Bex/hardware planejada, não codificada. Paleta definitiva pendente. Ambiguidades do combo e do ER registradas.

2026-11-22 — Implementação funcional:
- Backend: modelo de domínio (V2/V3), autenticação JWT com papéis, bloqueio, Google, recuperação por e-mail, catálogo, reserva concorrente, pagamentos idempotentes, estornos, ciclos com etapas, relatórios, gestão administrativa, agendador. Starter Flyway adicionado (o Spring Boot 4 não autoconfigura o Flyway só com `flyway-core`).
- Integrações por portas: Bex e hardware como padrão de produção (falham de forma explícita até haver contrato); sandbox e simulador para homologação (AD-007).
- Android: todas as telas T01–T07 e as previstas (cadastro, recuperação, acompanhamento, histórico, carteira, perfil, gestão de máquinas e preços), identidade do guia de design (substitui o roxo do protótipo).
- Banco: `READ COMMITTED` no pool para a reserva atômica (AD-008).
- Combo modelado como etapas de ciclo (AD-010). Durações dos serviços pendentes de confirmação.
- Ferramentas: Maven Wrapper e Gradle Wrapper JAR adicionados; ver `utilitarios.md`.
