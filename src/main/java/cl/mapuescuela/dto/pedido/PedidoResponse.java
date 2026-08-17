package cl.mapuescuela.dto.pedido;

import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.ModalidadEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String codigo,
        String nombreCliente,
        String emailCliente,
        String telefonoCliente,
        String direccionDespacho,
        ModalidadEntrega modalidadEntrega,
        EstadoPedido estado,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        List<DetallePedidoResponse> detalles
) {
}
