package br.com.lavicameta.repository;

import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.Usuario;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagamentoRepository extends JpaRepository<Pagamento, String> {

    Optional<Pagamento> findByProvedorAndTransacaoId(String provedor, String transacaoId);

    List<Pagamento> findByPedidoAndStatus(Pedido pedido, StatusPagamento status);

    Optional<Pagamento> findFirstByPedidoOrderByCriadoEmDesc(Pedido pedido);

    Optional<Pagamento> findFirstByPedidoAndStatusIn(Pedido pedido, Collection<StatusPagamento> status);

    List<Pagamento> findByStatus(StatusPagamento status);

    @EntityGraph(attributePaths = {"pedido", "pedido.servico", "pedido.maquina"})
    @Query("select p from Pagamento p where p.pedido.usuario = :usuario order by p.criadoEm desc")
    Page<Pagamento> findByUsuario(@Param("usuario") Usuario usuario, Pageable pageable);

    /** Soma dos pagamentos confirmados (aprovados e não estornados) no intervalo — RN09. */
    @Query("""
            select coalesce(sum(p.valor), 0) from Pagamento p
            where p.status = br.com.lavicameta.entity.StatusPagamento.APROVADO
              and p.pagoEm >= :inicio and p.pagoEm < :fim
            """)
    BigDecimal somarConfirmados(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query("""
            select count(p) from Pagamento p
            where p.status = br.com.lavicameta.entity.StatusPagamento.APROVADO
              and p.pagoEm >= :inicio and p.pagoEm < :fim
            """)
    long contarConfirmados(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    /**
     * Ganhos (RN09): valor dos pedidos pagos, datados pela confirmação do pagamento.
     * Retorna pares [pagoEm, valorTotal] para agregação por semana/mês no fuso local.
     */
    @Query("""
            select p.pagoEm, p.pedido.valorTotal from Pagamento p
            where p.status = br.com.lavicameta.entity.StatusPagamento.APROVADO
              and p.pedido.status in :statusPagos
              and p.pagoEm >= :inicio and p.pagoEm < :fim
            """)
    List<Object[]> listarGanhos(@Param("inicio") Instant inicio, @Param("fim") Instant fim,
                                @Param("statusPagos") Collection<br.com.lavicameta.entity.StatusPedido> statusPagos);
}
