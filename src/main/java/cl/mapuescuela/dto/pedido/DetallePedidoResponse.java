package cl.mapuescuela.dto.pedido;

import java.math.BigDecimal;

public record DetallePedidoResponse(
        Long id,
        Long productoId,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
