# Registro de decisões

2026-11-18 — Estrutura inicial criada a partir dos dois anexos. Sem funcionalidades de domínio. Android Java MVVM; backend Java 21/Spring Boot 4.1.2; MySQL/Flyway; integração Bex/hardware planejada, não codificada. Paleta definitiva pendente. Ambiguidades do combo e do ER registradas.

2026-11-22 — Implementação funcional:
- Backend: modelo de domínio (V2/V3), autenticação JWT com papéis, bloqueio, Google, recuperação por e-mail, catálogo, reserva concorrente, pagamentos idempotentes, estornos, ciclos com etapas, relatórios, gestão administrativa, agendador. Starter Flyway adicionado (o Spring Boot 4 não autoconfigura o Flyway só com `flyway-core`).
- Integrações por portas: Bex e hardware como padrão de produção (falham de forma explícita até haver contrato); sandbox e simulador para homologação (AD-007).
- Android: todas as telas T01–T07 e as previstas (cadastro, recuperação, acompanhamento, histórico, carteira, perfil, gestão de máquinas e preços), identidade do guia de design (substitui o roxo do protótipo; a identidade foi revisada em 2026-11-25).
- Banco: `READ COMMITTED` no pool para a reserva atômica (AD-008).
- Combo modelado como etapas de ciclo (AD-010). Durações dos serviços pendentes de confirmação.
- Ferramentas: Maven Wrapper e Gradle Wrapper JAR adicionados; ver `utilitarios.md`.

2026-11-25 — Nova identidade visual (guia de design revisado: roxo #45257A, lilás #9B7FE0, cinzas e branco; Outfit):
- Android: tokens de cor renomeados (`roxo`, `roxo_escuro`, `lilas`, neutros e estados) e azuis removidos; tema com fundo cinza-névoa e superfícies brancas; botões e campos com 52 dp, cabeçalho com 56 dp, títulos 28/22/18 sp (tablet 40/32/24), altura de linha 1,5 / 1,2; foco dos campos em lilás; abas com sublinhado lilás; status das máquinas conforme o guia (Disponível verde, Em uso/Reservada âmbar, Manutenção cinza); indicador de ciclo em anel lilás com o tempo no centro.
- Logos e ícone do app recortados das variações oficiais em `assets/img` sem alterar o desenho (`logo_principal`, `logo_vertical`, nova `logo_nome` no cabeçalho do Início, `ic_launcher`).
- Removido o slogan da tela de login: o guia define que a marca não possui slogan.
- Sem alteração de regras de negócio, rotas, contratos de API ou banco.
