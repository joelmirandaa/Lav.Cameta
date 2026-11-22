package br.com.lavicameta.repository;

import br.com.lavicameta.entity.MetodoPagamentoSalvo;
import br.com.lavicameta.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoPagamentoSalvoRepository extends JpaRepository<MetodoPagamentoSalvo, String> {

    List<MetodoPagamentoSalvo> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    Optional<MetodoPagamentoSalvo> findByIdAndUsuario(String id, Usuario usuario);
}
