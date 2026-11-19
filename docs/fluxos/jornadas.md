# Jornadas e fluxos

Origem: seções 3, 8 e 9 do PDF, páginas 5–6 e 11–13. T01–T07 são telas descritas; telas adicionais estão **previstas e não prototipadas**.

```mermaid
flowchart TD
  A[T01 Login] --> B[T02 Máquinas]
  A --> C[Cadastro] --> B
  A --> D[Recuperar senha] --> A
  B --> E{Disponível?}
  E -- Não --> B
  E -- Sim --> F[T03 Detalhe e serviço]
  F --> G[Pedido e reserva de 5 min]
  G --> H[T04 Checkout PIX ou cartão]
  H --> I{Confirmação validada no servidor?}
  I -- Não e expirou --> J[Cancelar e liberar reserva]
  I -- Sim --> K[Comando à máquina]
  K --> L{Início confirmado?}
  L -- Sim --> M[Acompanhar ciclo e finalizar]
  L -- Não --> N[Estorno integral e revisão]
  M --> B
```

```mermaid
flowchart LR
 A[T05 Login admin] --> B[T06 Relatório mensal]
 B <--> C[T07 Relatório anual]
 B --> D[Escolha período]
 C --> D
 B --> E[Gestão máquinas e preços: tela pendente]
 C --> E
```

## Matriz fluxo → sistema
| Fluxo | Telas | Endpoints planejados, não implementados | Entidades | Regras |
|---|---|---|---|---|
| Autenticar | T01, Cadastro, Recuperação | `/auth/*` | usuarios | RN011, RN012 |
| Consultar | T02, T03 | `GET /machines`, `GET /machines/{id}` | maquinas, servicos | RN002, RN007 |
| Reservar | T03, T04 | `POST /orders` | pedidos, maquinas | RN001, RN003, RN005, RN008 |
| Pagar | T04 | `POST /orders/{id}/checkout`, webhook a definir | pagamentos, pedidos | RN004, RN013 |
| Iniciar/acompanhar | Acompanhamento | `GET /orders/{id}/cycle` | ciclos, maquinas | RN004, RN006 |
| Relatórios | T05–T07 | `GET /admin/reports` | pedidos, pagamentos | RN009–RN011 |
| Administração | Não prototipada | `GET/PUT /admin/machines` | maquinas, servicos | RN001, RN007 |

Endpoints são **propostos** e exigem revisão da autenticação, versão e integração Bex.
