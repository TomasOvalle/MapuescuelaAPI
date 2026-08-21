package cl.mapuescuela.dto.pedido;

import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.ModalidadEntrega;

public record CambioEstadoPedidoResponse(
        Long pedidoId,
        String codigoPedido,
        EstadoPedido estadoAnterior,
        EstadoPedido estadoActual,
        ModalidadEntrega modalidadEntrega,
        String mensaje
) {
}
