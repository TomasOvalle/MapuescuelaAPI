package cl.mapuescuela.repository;

import cl.mapuescuela.entity.ComprobantePago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {
    Optional<ComprobantePago> findByPedidoId(Long pedidoId);

    boolean existsByPedidoId(Long pedidoId);
}
