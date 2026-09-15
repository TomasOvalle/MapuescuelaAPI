package cl.mapuescuela.dto.despacho;

import cl.mapuescuela.entity.TipoDespacho;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DespachoRequest(
        @NotNull(message = "El pedido es obligatorio")
        Long pedidoId,

        @NotNull(message = "El tipo de despacho es obligatorio")
        TipoDespacho tipo,

        @Size(
                max = 120,
                message = "La empresa de transporte no puede superar los 120 caracteres"
        )
        String empresaTransporte,

        @Size(
                max = 120,
                message = "El número de seguimiento no puede superar los 120 caracteres"
        )
        String numeroSeguimiento,

        @NotNull(message = "La fecha de envío es obligatoria")
        LocalDate fechaEnvio
) {
}
