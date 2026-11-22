-- Apoio operacional: confere os totais do relatório mensal (RN09) direto no banco.
-- Datas em UTC; ajuste o intervalo para o mês desejado no fuso America/Belem (UTC-3).
SET @inicio = '2026-11-01 03:00:00', @fim = '2026-12-01 03:00:00';
SELECT COALESCE(SUM(pe.valor_total), 0) AS ganhos
  FROM pagamentos pa JOIN pedidos pe ON pe.id = pa.pedido_id
 WHERE pa.status = 'APROVADO' AND pe.status IN ('PAGO','EM_ANDAMENTO','CONCLUIDO')
   AND pa.pago_em >= @inicio AND pa.pago_em < @fim;
SELECT COUNT(*) AS quantidade, COALESCE(SUM(valor), 0) AS pagamentos_confirmados
  FROM pagamentos WHERE status = 'APROVADO' AND pago_em >= @inicio AND pago_em < @fim;
