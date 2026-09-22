package cl.mapuescuela.process;

import cl.mapuescuela.service.PagoService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("aprobarPagoYDescontarStockDelegate")
public class AprobarPagoYDescontarStockDelegate implements JavaDelegate {
    private final PagoService pagoService;

    public AprobarPagoYDescontarStockDelegate(
            PagoService pagoService
    ) {
        this.pagoService = pagoService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        Long pedidoId =
                ProcesoVentaVariables.obtenerPedidoId(execution);

        pagoService.aprobarPagoDesdeProceso(pedidoId);
    }
}
