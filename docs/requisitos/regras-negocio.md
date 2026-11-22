# Regras de negócio

Origem: PDF seção 4, página 6. Todas **Confirmado**. IDs RN001–RN013 equivalem RN01–RN13.

| ID | Regra |
|---|---|
| RN001 | Lavar R$ 12, Secar R$ 10, combo R$ 18; preços configuráveis pelo admin. |
| RN002 | Contratar somente máquina disponível. |
| RN003 | Reservar máquina por 5 minutos no pagamento. |
| RN004 | Iniciar ciclo só após confirmação de pagamento. |
| RN005 | Expirar pedido e liberar máquina sem pagamento em 5 minutos. |
| RN006 | Falha ao iniciar após pagamento gera estorno integral. |
| RN007 | Máquina em manutenção não permite contratação. |
| RN008 | Combo reserva lavadora e depois secadora; total R$ 18. |
| RN009 | Ganhos = soma de pedidos pagos; pagamentos = confirmados/compensados. |
| RN010 | Variação = (atual − anterior) / anterior × 100. |
| RN011 | Relatórios apenas para admin autenticado. |
| RN012 | Nunca armazenar dados completos de cartão. |
| RN013 | Um pedido possui exatamente um pagamento aprovado. |

## Atenções
- **Implementado (Proposto):** RN008 resolvido com etapas de ciclo (pedido 1—N ciclos, um por etapa); ver AD-010.
- **Implementado:** RN010 com período anterior = 0 retorna variação nula e o app exibe “sem base comparável”.
- **Implementado:** idempotência de pagamento e de início de ciclo; trava transacional da máquina. A reserva é exposta como indicador `reservada`, sem novo status persistido.
- **Inconsistência do PDF:** CT03 referencia RN12 mas descreve bloqueio após 5 tentativas; CT16 menciona RN15 inexistente; CT24 referencia RN13 mas fala de cartão. Corrigir rastreabilidade antes da execução dos testes.
