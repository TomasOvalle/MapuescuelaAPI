package cl.mapuescuela.dto.comprobante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComprobantePagoRequest(
        @NotBlank(message = "El nombre del archivo es obligatorio")
        @Size(max = 255, message = "El nombre del archivo no puede superar los 255 caracteres")
        String nombreArchivo,

        @NotBlank(message = "La ruta del archivo es obligatoria")
        @Size(max = 500, message = "La ruta del archivo no puede superar los 500 caracteres")
        String rutaArchivo,

        @Size(max = 1000, message = "La observación no puede superar los 1000 caracteres")
        String observacion
) {
}
