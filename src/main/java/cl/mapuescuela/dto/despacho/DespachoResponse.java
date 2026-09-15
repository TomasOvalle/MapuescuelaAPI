package cl.mapuescuela.dto.despacho;

import cl.mapuescuela.entity.TipoDespacho;

import java.time.LocalDate;

public record DespachoResponse(
        Long id,
        Long pedidoId,
        String codigoPedido,
        TipoDespacho tipo,
        String empresaTransporte,
        String numeroSeguimiento,
        LocalDate fechaEnvio
) {
}
