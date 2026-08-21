package cl.mapuescuela.dto.pago;

import jakarta.validation.constraints.Size;

public record RevisionPagoRequest(
        @Size(max = 1000, message = "La observación no puede superar los 1000 caracteres")
        String observacion
) {
}
