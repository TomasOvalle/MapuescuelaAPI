package cl.mapuescuela.process;

import cl.mapuescuela.service.PagoService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("rechazarPagoDelegate")
public class RechazarPagoDelegate implements JavaDelegate {
    private final PagoService pagoService;

    public RechazarPagoDelegate(
            PagoService pagoService
    ) {
        this.pagoService = pagoService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        Long pedidoId =
                ProcesoVentaVariables.obtenerPedidoId(execution);

        pagoService.rechazarPagoDesdeProceso(pedidoId);
    }
}
