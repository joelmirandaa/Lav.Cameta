# Contratos e integrações — proposta, não implementados

## Endpoints REST candidatos
- `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, recuperação a definir.
- `GET /api/v1/machines`, `GET /api/v1/machines/{id}` e `GET /api/v1/services`.
- `POST /api/v1/orders` cria reserva transacional.
- `POST /api/v1/orders/{id}/checkout` obtém checkout do backend, **sem especificar contrato Bex ainda**.
- `GET /api/v1/orders/{id}` e `GET /api/v1/orders/{id}/cycle`.
- `GET /api/v1/admin/reports?period=month|year` e gestão de máquinas/preços.
- `GET /api/v1/health` é o **único endpoint realmente implementado** (health técnico).

## Integração Bex
**Decisão confirmada no texto de instruções:** usar checkout Bex. **Pendente:** documentação oficial, hostname, endpoints, mecanismo de autenticação, webhook, assinatura, estorno, idempotência, sandbox e capacidade de crédito/débito. Evitar presumir qualquer parâmetro ou evento.

Sequência segura proposta: app solicita pedido ao backend → backend reserva sob trava → cria sessão Bex por adaptador → app abre checkout → backend verifica confirmação de forma autenticada → grava aprovação sem duplicação → despacha comando à máquina → acompanha conclusão. Redirecionamento ao app é apenas UX, nunca prova de pagamento.

## Máquina
**Pendente:** interface de controle, identificador físico, protocolo, ciclo/telemetria e falhas. Criar contrato `MachineGateway` quando houver especificação; não simular operação real como concluída.

## Segurança de transporte
TLS; secrets somente ambiente; autorização por perfil; dados de cartão fora da API e do banco; auditoria sem vazamento de PII.
