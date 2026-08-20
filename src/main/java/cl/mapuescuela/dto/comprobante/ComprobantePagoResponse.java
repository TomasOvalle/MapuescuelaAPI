package cl.mapuescuela.dto.comprobante;

import cl.mapuescuela.entity.DecisionPago;

import java.time.LocalDateTime;

public record ComprobantePagoResponse(
        Long id,
        Long pedidoId,
        String archivoUrl,
        String observacion,
        DecisionPago decision,
        LocalDateTime fechaCarga
) {
}
