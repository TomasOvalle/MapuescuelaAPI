package cl.mapuescuela.process;

import cl.mapuescuela.service.PedidoService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("cancelarPedidoDelegate")
public class CancelarPedidoDelegate implements JavaDelegate {
    private final PedidoService pedidoService;

    public CancelarPedidoDelegate(
            PedidoService pedidoService
    ) {
        this.pedidoService = pedidoService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        Long pedidoId =
                ProcesoVentaVariables.obtenerPedidoId(execution);

        pedidoService.cancelarPorVencimiento(pedidoId);
    }
}
