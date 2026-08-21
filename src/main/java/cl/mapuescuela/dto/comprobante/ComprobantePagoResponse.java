package cl.mapuescuela.dto.comprobante;

import cl.mapuescuela.entity.DecisionPago;

import java.time.LocalDateTime;

public record ComprobantePagoResponse(
        Long id,
        Long pedidoId,
        String codigoPedido,
        String nombreArchivo,
        String rutaArchivo,
        LocalDateTime fechaCarga,
        DecisionPago decision,
        String observacion
) {
}
