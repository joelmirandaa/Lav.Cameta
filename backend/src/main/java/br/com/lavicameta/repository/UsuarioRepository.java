package br.com.lavicameta.repository;

import br.com.lavicameta.entity.TipoUsuario;
import br.com.lavicameta.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByTelefone(String telefone);

    Optional<Usuario> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    boolean existsByTelefone(String telefone);

    boolean existsByEmailAndIdNot(String email, String id);

    boolean existsByTelefoneAndIdNot(String telefone, String id);

    boolean existsByTipo(TipoUsuario tipo);
}
