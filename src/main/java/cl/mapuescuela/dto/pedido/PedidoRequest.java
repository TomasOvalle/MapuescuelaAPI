package cl.mapuescuela.dto.pedido;

import cl.mapuescuela.entity.ModalidadEntrega;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PedidoRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(
                max = 120,
                message = "El nombre del cliente no puede superar los 120 caracteres"
        )
        String nombreCliente,

        @NotBlank(message = "El email del cliente es obligatorio")
        @Email(message = "El email del cliente no es válido")
        @Size(
                max = 150,
                message = "El email no puede superar los 150 caracteres"
        )
        String emailCliente,

        @NotBlank(message = "El teléfono del cliente es obligatorio")
        @Size(
                max = 30,
                message = "El teléfono no puede superar los 30 caracteres"
        )
        String telefonoCliente,

        @Size(
                max = 300,
                message = "La dirección no puede superar los 300 caracteres"
        )
        String direccionDespacho,

        @NotNull(message = "La modalidad de entrega es obligatoria")
        ModalidadEntrega modalidadEntrega,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        @Valid
        List<DetallePedidoRequest> detalles
) {
}
