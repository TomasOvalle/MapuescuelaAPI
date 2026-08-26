package cl.mapuescuela.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

//Este se utilizara para:
// Producto no encontrado
// Pedido no encontrado
// Comprobante no encontrado