package cl.mapuescuela.repository;

import cl.mapuescuela.entity.EstadoProducto;
import cl.mapuescuela.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEstadoAndStockGreaterThan(EstadoProducto estado, Integer stock);
}
