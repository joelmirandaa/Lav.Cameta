package br.com.lavicameta.repository;

import br.com.lavicameta.entity.Ciclo;
import br.com.lavicameta.entity.EtapaCiclo;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusCiclo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CicloRepository extends JpaRepository<Ciclo, String> {

    @EntityGraph(attributePaths = {"maquina"})
    List<Ciclo> findByPedidoOrderByInicioEmAsc(Pedido pedido);

    Optional<Ciclo> findByPedidoAndEtapa(Pedido pedido, EtapaCiclo etapa);

    @EntityGraph(attributePaths = {"maquina", "pedido"})
    List<Ciclo> findByStatus(StatusCiclo status);

    List<Ciclo> findByStatusAndFimPrevistoEmLessThanEqual(StatusCiclo status, Instant limite);
}
