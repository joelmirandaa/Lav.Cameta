# Plano de testes

**Fonte:** PDF seção 10, páginas 13–15. Ambiente esperado: Android 12+, servidor homologação, gateway sandbox e máquina simulada. Critério: 100% dos testes de alta prioridade passando, sem defeitos críticos.

CT01–CT05: login válido/inválido, bloqueio, Google, recuperação. CT06–CT11: máquinas, filtros, indisponibilidade, preço. CT12–CT16: aprovação/expiração PIX, cartão recusado, falha de máquina, concorrência. CT17: histórico. CT18–CT24: admin, relatórios, variação, atualização e cartão tokenizado. CT25: 200 usuários simultâneos, resposta abaixo de 2 s.

**Problemas de rastreabilidade no documento:** CT03 usa RN12 indevida; CT16 menciona RN15 inexistente; CT24 usa RN13 indevida; CT25 cita RNF02 embora este se refira à atualização de status, e não à latência de resposta. CT22: cálculo `(1247,50 - 1108,90) / 1108,90 * 100` ≈ 12,50%, conforme fonte.

**Testes propostos adicionais:** checkout redirecionado sem confirmação não ativa máquina; callbacks duplicados aprovam uma vez; dois clientes concorrentes reservam apenas uma vez; estorno idempotente; desconexão temporária de hardware; usuário cliente acessando `/admin` negado; comparação de valores e pedido no webhook; cenário de dois ciclos no combo após definição do modelo.

**Executado nesta entrega:** somente validação estática e, se disponível, build do esqueleto. Nenhum dos CT01–CT25 foi executado.
