package br.com.lavicameta.repository;

import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.TipoMaquina;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaquinaRepository extends JpaRepository<Maquina, Integer> {

    List<Maquina> findAllByOrderByTipoAscNomeAsc();

    List<Maquina> findByTipoOrderByNomeAsc(TipoMaquina tipo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);

    /** Trava a linha da máquina para serializar reservas concorrentes (RN03, CT16). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Maquina m where m.id = :id")
    Optional<Maquina> findByIdForUpdate(@Param("id") Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Maquina m where m.tipo = :tipo and m.status = :status order by m.nome")
    List<Maquina> findByTipoAndStatusForUpdate(@Param("tipo") TipoMaquina tipo, @Param("status") StatusMaquina status);
}
