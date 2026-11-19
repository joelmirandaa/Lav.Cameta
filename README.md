# Lav.cameta

Base inicial de engenharia do **Lav.Cametá**, aplicativo Android nativo para uma lavanderia de autoatendimento. **Estado:** estrutura de projeto, documentação e inicialização técnica; funcionalidades de produção não implementadas.

## Fontes e classificação
- `Lav.Cametá – Documentação do Sistema.pdf`, v1.0, novembro/2026: fonte de requisitos, sete telas descritas, regras, modelo ER e testes.
- `Texto colado(3).txt`: instrução de estrutura, linguagem e integração Bex.
- **Confirmado** = expresso na documentação; **Proposto** = decisão arquitetural inicial; **Pendente** = exige informação externa ou validação.

## Público e operação
Cliente: cria conta, consulta lavadoras/secadoras, seleciona serviço, paga e acompanha o ciclo. Administrador: acesso separado, relatórios financeiros mensais/anuais e gestão de máquinas/preços. Gateway e máquina são integrações externas.

## Fluxos centrais
1. Login/cadastro/recuperação → lista das máquinas → filtro → detalhes → serviço → reserva de 5 min → checkout → confirmação confiável no backend → comando à máquina → ciclo/encerramento.
2. Administrador → login restrito → relatório Mês/Ano → calendário → consulta, além de gestão de máquinas/preços prevista.
3. Se pagamento expirar: pedido expira e reserva é liberada. Se comando de início falhar depois do pagamento: fluxo de estorno integral, condicionado às capacidades do gateway.
Veja [fluxos](docs/fluxos/jornadas.md), [requisitos](docs/requisitos/requisitos.md) e [regras](docs/requisitos/regras-negocio.md).

## Tecnologia e arquitetura
- **Android:** Java, Android Studio, Gradle 8.13, Android Gradle Plugin 8.13.2, compile/target SDK 35, min SDK 31 (Android 12); estrutura MVVM/Repository e UI inicial mínima.
- **API:** Java 21, Spring Boot 4.1.2, Maven, API REST, camadas controller/service/repository/entity/dto, validação, segurança prevista.
- **Persistência:** MySQL de produção; Flyway em `backend/src/main/resources/db/migration` (única fonte executável). Scripts em `database/scripts` destinam-se a apoio operacional.
- **Integração Bex:** adaptador separado, contratos **não definidos** até receber documentação oficial. Nenhuma chamada externa ou checkout real está implementado.
- **Integração de máquinas:** porta/adaptador a especificar após obtenção de protocolo e homologação do hardware.

## Como abrir
**Android:** abrir a pasta `android/` no Android Studio com JDK 17+ e Android SDK 35. O Gradle wrapper aponta para Gradle 8.13. `./gradlew assembleDebug` (Linux/macOS) ou `gradlew.bat assembleDebug` (Windows); exige acesso às dependências e SDK.

**Backend:** com Java 21 e Maven instalado, em `backend/`, definir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` no ambiente e executar `mvn spring-boot:run`. O MySQL deve estar ativo, com o schema `lavicameta` criado previamente. Sem DB configurado o backend não sobe por design. Testar `GET /api/v1/health` após iniciar. **Não há endpoints de negócio prontos**.

Exemplo (somente valores locais ilustrativos, sem senha padrão):
```bash
export DB_URL='jdbc:mysql://localhost:3306/lavicameta?useSSL=false&allowPublicKeyRetrieval=true'
export DB_USERNAME='seu_usuario'
export DB_PASSWORD='defina_localmente'
cd backend && mvn spring-boot:run
```

## Diretórios
- `android/`: app Android, tema centralizado, tela inicial de orientação.
- `backend/`: aplicação Spring Boot, configuração, health e migration inicial estrutural.
- `database/scripts/`: SQL auxiliar não executado por Flyway.
- `docs/`: requisitos, fluxos, arquitetura, dados, API, funcionalidades, decisões.
- `AGENTS.md`: disciplina obrigatória dos agentes e critérios de alteração.
- `STYLE.md`: padrões de estilo, sem inventar identidade definitiva.

## Escopo da primeira entrega
Incluídos: documentação rastreável, estrutura de diretórios, configurações de projeto, tela inicial ilustrativa e endpoint técnico de saúde, teste básico do backend. **Não incluídos:** login real, reservas, pagamentos, integração Bex, controle físico das máquinas, dashboard pronto ou ambiente de produção.

## Planejamento
1. Confirmar requisitos e fechar pendências de gateway/máquina.
2. Preparar ambiente e credenciais locais.
3. Modelar banco e migrações de domínio.
4. Implementar autenticação, catálogo, reserva, pagamentos e ciclo no backend.
5. Construir telas e estados Android.
6. Integrar gateway Bex e hardware em sandbox.
7. Testar concorrência, idempotência, estornos, segurança e desempenho.
8. Preparar produção e monitoramento.

Prioridade MVP: autenticação, lista/estado de máquinas, seleção e preço, reserva, PIX, confirmação e partida segura, relatórios administrativos básicos. Cartão, Google, carteira e perfil avançado podem entrar depois, respeitando prioridades do PDF.

## Pendências mais importantes
- Documentação técnica Bex, autenticação, webhook/consulta, ambiente sandbox, suporte PIX/cartão/estorno.
- Fabricante, protocolo, conectividade, telemetria e controle de início das máquinas.
- Especificação do combo Lavar + Secar com duas máquinas, pois o modelo original guarda apenas `maquina_id` no pedido.
- Identidade visual: protótipo WashFlow roxo é referência, não definição oficial final do Lavicameta.
- Semântica financeira dos relatórios e tratamento de divisão por zero na variação.

## Documentação
[Arquitetura](docs/arquitetura/decisoes.md) · [API](docs/api/contratos.md) · [Modelo de dados](docs/banco-de-dados/modelagem.md) · [Módulos](docs/funcionalidades/modulos.md) · [Plano de testes](docs/requisitos/testes.md) · [Mudanças](docs/alteracoes/registro.md) · [Validação da entrega](docs/alteracoes/validacao.md).
