package br.com.lavicameta.repository;

import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.entity.Usuario;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, String> {

    @EntityGraph(attributePaths = {"maquina", "servico"})
    Page<Pedido> findByUsuarioOrderByCriadoEmDesc(Usuario usuario, Pageable pageable);

    @EntityGraph(attributePaths = {"maquina", "servico", "usuario"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> findDetalhadoById(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> findByIdForUpdate(@Param("id") String id);

    /** Existe reserva vigente (aguardando pagamento e não expirada) para a máquina? */
    @Query("""
            select count(p) > 0 from Pedido p
            where p.maquina.id = :maquinaId and p.status = br.com.lavicameta.entity.StatusPedido.AGUARDANDO_PAGAMENTO
              and p.expiraEm > :agora
            """)
    boolean existsReservaVigente(@Param("maquinaId") Integer maquinaId, @Param("agora") Instant agora);

    @Query("""
            select p.maquina.id from Pedido p
            where p.status = br.com.lavicameta.entity.StatusPedido.AGUARDANDO_PAGAMENTO and p.expiraEm > :agora
            """)
    List<Integer> findMaquinasComReservaVigente(@Param("agora") Instant agora);

    @Query("select p.id from Pedido p where p.status = :status and p.expiraEm <= :agora")
    List<String> findIdsByStatusAndExpiraEmBefore(@Param("status") StatusPedido status, @Param("agora") Instant agora);

    @Query("select p.id from Pedido p where p.status = :status")
    List<String> findIdsByStatus(@Param("status") StatusPedido status);

    boolean existsByUsuarioAndStatusIn(Usuario usuario, Collection<StatusPedido> status);

    boolean existsByMaquinaIdAndStatusIn(Integer maquinaId, Collection<StatusPedido> status);
}
