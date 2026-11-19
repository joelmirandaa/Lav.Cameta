# Módulos e cobertura

| Módulo | Origem | Escopo | Estado |
|---|---|---|---|
| Autenticação cliente | RF01–04 | Cadastro, login, Google, recuperação | Planejado |
| Catálogo máquinas | RF05–07 | Consulta, filtros, detalhes e status | Planejado |
| Serviço/reserva | RF08–09, RN01–05 | Preço, pedido e reserva atômica | Planejado |
| Pagamento | RF10–13 | PIX, cartão e confirmação segura Bex | Aguardando contrato |
| Ciclo | UC08, RN06/08 | Comando, tempo restante, encerramento | Aguardando hardware |
| Conta | RF14–16 | Histórico, carteira e perfil | Planejado |
| Administração | RF17–21, UC15 | Login, relatório mensal/anual, máquinas/preços | Planejado |

Proposta: começar autenticação → catálogo → reserva, em paralelo validar integração Bex/hardware. Telas não prototipadas exigem design antes da produção.
