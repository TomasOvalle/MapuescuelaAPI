package cl.mapuescuela.dto.carrito;

import java.math.BigDecimal;

public record ItemCarritoResponse(
        Long id,
        Long productoId,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
