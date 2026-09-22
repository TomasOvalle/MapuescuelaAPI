package cl.mapuescuela.process;

import org.flowable.engine.delegate.DelegateExecution;

public class ProcesoVentaVariables {
    public static final String PEDIDO_ID = "pedidoId";
    public static final String MODALIDAD_ENTREGA = "modalidadEntrega";
    public static final String PAGO_APROBADO = "pagoAprobado";
    public static final String PLAZO_COMPROBANTE = "plazoComprobante";

    private ProcesoVentaVariables() {
    }

    public static Long obtenerPedidoId(DelegateExecution execution) {

        Object valor = execution.getVariable(PEDIDO_ID);

        if (valor == null) {
            throw new IllegalStateException(
                    "La variable de proceso 'pedidoId' es obligatoria"
            );
        }

        if (valor instanceof Number numero) {
            return numero.longValue();
        }

        try {
            return Long.valueOf(valor.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    "La variable 'pedidoId' no contiene un identificador válido: "
                            + valor
            );
        }
    }
}
