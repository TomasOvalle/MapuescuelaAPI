package cl.mapuescuela.repository;

import cl.mapuescuela.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    Optional<Despacho> findByPedidoId(Long pedidoId);
}
