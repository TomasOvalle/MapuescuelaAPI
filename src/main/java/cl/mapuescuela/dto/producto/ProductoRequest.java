package cl.mapuescuela.dto.producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio" )
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotBlank(message = "La categoría es obligatoria")
        @Size(max = 80, message = "La categoría no puede superar los 80 caracteres")
        String categoria,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        BigDecimal precio,

        @Size(max = 500, message = "La URL de la foto no puede superar los 500 caracteres")
        String fotoUrl,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock
) {}
