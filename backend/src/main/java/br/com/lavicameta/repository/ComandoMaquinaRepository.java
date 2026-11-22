package br.com.lavicameta.repository;

import br.com.lavicameta.entity.ComandoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComandoMaquinaRepository extends JpaRepository<ComandoMaquina, Long> {

    boolean existsByChaveIdempotencia(String chaveIdempotencia);
}
