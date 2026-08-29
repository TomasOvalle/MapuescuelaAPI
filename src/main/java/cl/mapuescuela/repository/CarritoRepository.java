package cl.mapuescuela.repository;

import cl.mapuescuela.entity.Carrito;
import cl.mapuescuela.entity.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByCodigo(String codigo);

    List<Carrito> findByEstado(EstadoCarrito estado);
}
