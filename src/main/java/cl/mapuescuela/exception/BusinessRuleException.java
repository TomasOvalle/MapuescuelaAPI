package cl.mapuescuela.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

//Este se utilizara para:
// Stock insuficiente
// Pedido no está en el estado correcto
// El comprobante ya fue revisado
// El pedido ya tiene comprobante