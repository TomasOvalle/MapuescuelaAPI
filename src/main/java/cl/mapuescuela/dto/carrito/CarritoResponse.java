package cl.mapuescuela.dto.carrito;

import cl.mapuescuela.entity.EstadoCarrito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponse(
        Long id,
        String codigo,
        EstadoCarrito estado,
        LocalDateTime fechaCreacion,
        BigDecimal total,
        List<ItemCarritoResponse> items
) {
}
