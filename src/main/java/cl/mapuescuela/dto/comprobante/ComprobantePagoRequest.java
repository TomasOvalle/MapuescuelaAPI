package cl.mapuescuela.dto.comprobante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComprobantePagoRequest(
        @NotBlank(message = "La URL del comprobante es obligatoria")
        @Size(max = 500, message = "La URL del comprobante no puede superar los 500 caracteres")
        String archivoUrl,

        @Size(max = 300, message = "La observación no puede superar los 300 caracteres")
        String observacion
) {
}
