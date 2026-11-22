package br.com.lavicameta.repository;

import br.com.lavicameta.entity.EventoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoPagamentoRepository extends JpaRepository<EventoPagamento, Long> {

    boolean existsByProvedorAndEventoId(String provedor, String eventoId);
}
