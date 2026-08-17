package cl.mapuescuela.exception;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String message) {
        super(message);
    }

    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado: " + id);
    }
}
