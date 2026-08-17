package cl.mapuescuela.dto.producto;

import cl.mapuescuela.entity.EstadoProducto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        String categoria,
        BigDecimal precio,
        String fotoUrl,
        Integer stock,
        EstadoProducto estado,
        Long version
) {
}
