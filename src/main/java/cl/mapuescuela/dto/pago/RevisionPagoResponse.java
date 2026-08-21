package cl.mapuescuela.dto.pago;

import cl.mapuescuela.entity.DecisionPago;
import cl.mapuescuela.entity.EstadoPedido;

public record RevisionPagoResponse(
        Long pedidoId,
        String codigoPedido,
        EstadoPedido estadoPedido,
        Long comprobanteId,
        DecisionPago decisionPago,
        String observacion
) {
}
