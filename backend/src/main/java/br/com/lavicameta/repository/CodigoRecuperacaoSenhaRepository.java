package br.com.lavicameta.repository;

import br.com.lavicameta.entity.CodigoRecuperacaoSenha;
import br.com.lavicameta.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoRecuperacaoSenhaRepository extends JpaRepository<CodigoRecuperacaoSenha, String> {

    Optional<CodigoRecuperacaoSenha> findFirstByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    List<CodigoRecuperacaoSenha> findByUsuarioAndUsadoEmIsNull(Usuario usuario);
}
