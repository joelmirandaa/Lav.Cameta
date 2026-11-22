package br.com.lavicameta.repository;

import br.com.lavicameta.entity.Servico;
import br.com.lavicameta.entity.TipoServico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    Optional<Servico> findByNome(TipoServico nome);

    List<Servico> findAllByOrderByIdAsc();
}
