# AGENTS.md — regras de desenvolvimento Lavicameta

Este documento é obrigatório para qualquer agente que modificar o projeto.

## Ordem de trabalho
1. Ler `AGENTS.md`, `README.md` e `STYLE.md`; consultar os documentos da funcionalidade.
2. Compreender pedido, requisitos de origem e arquivos envolvidos.
3. Inspecionar implementações, testes, contratos e dependências existentes.
4. Propor mudança mínima; distinguir confirmado, inferido, proposto e pendente.
5. Programar com responsabilidades separadas e sem mudanças colaterais.
6. Executar verificações possíveis e atualizar testes/documentação necessária.
7. Entregar resumo de objetivo, mudanças, arquivos, testes e pendências.

## Código e arquitetura
- Java nativo Android MVVM: UI/Activity/Fragment **não** executa regras de negócio nem consulta MySQL diretamente; ViewModel coordena casos de uso e Repository lida com fontes de dados.
- Spring Boot em camadas: Controller recebe/valida, Service processa regras/transações, Repository persiste, Integration encapsula Bex/máquinas, DTO delimita fronteiras.
- Classes/interfaces `PascalCase`; campos/métodos `camelCase`; constantes `UPPER_SNAKE_CASE`; packages minúsculos; indentação 4 espaços; imports organizados.
- Aplicar SOLID/DRY/KISS com parcimônia: alta coesão, baixo acoplamento, sem abstração prematura, código morto ou classes genéricas gigantes.
- Não alterar contratos ou regras de domínio sem atualizar testes, documentação e plano de migração.
- Preservar compatibilidade com a configuração de Java/Gradle/Maven documentada; justificar dependências novas.
- Preferir alterações pequenas e revisáveis, sem refatorações globais oportunistas.

## Banco e integrações
- Nunca conectar Android diretamente ao MySQL.
- Flyway executa **somente** migrations em `backend/src/main/resources/db/migration`; não duplicar scripts executáveis em `database/`.
- Não usar `ddl-auto=create` ou `update` em produção; migrações versionadas com revisão.
- Sem segredo em commit/log/ZIP: senha, token, credencial e chave sempre em variáveis de ambiente ou gerenciador apropriado.
- Checkout não equivale a pagamento aprovado. A confirmação deve ser autenticada e verificada pelo backend.
- Reservas e pagamentos exigem controle concorrente, idempotência e auditoria; não iniciar máquina duas vezes em reentregas.
- Falha em comando após pagamento requer compensação/estorno rastreável, nunca simulação silenciosa de sucesso.
- Não assumir endpoints do Bex nem comandos de máquina antes de conferir contratos oficiais.

## Segurança, LGPD e qualidade
- HTTPS/TLS, controle de acesso no backend, hashing seguro, sem dados completos de cartão, logs minimizados e política de retenção a definir.
- Testar concorrência, expiração de 5 minutos, duplicidade de eventos, falha de rede, estorno e acesso indevido ao admin.
- Usar `STYLE.md` para componentes e identidade; não inventar paleta oficial.
- Não apagar funcionalidades existentes, mudar regra de negócio estranha à tarefa, inventar requisitos, criar pastas arbitrárias nem anunciar testes não realizados.

## Comunicação de resultados
Relatar sucintamente objetivo, arquivos alterados, comportamento comprovado, comandos/testes efetivamente executados, limitações e decisões pendentes. Não tratar código de estrutura ou mock como integração em produção.
