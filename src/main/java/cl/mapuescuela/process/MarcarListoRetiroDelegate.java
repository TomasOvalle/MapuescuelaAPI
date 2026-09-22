package cl.mapuescuela.process;

import cl.mapuescuela.service.PreparacionPedidoService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("marcarListoRetiroDelegate")
public class MarcarListoRetiroDelegate implements JavaDelegate {
    private final PreparacionPedidoService preparacionPedidoService;

    public MarcarListoRetiroDelegate(
            PreparacionPedidoService preparacionPedidoService
    ) {
        this.preparacionPedidoService =
                preparacionPedidoService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        Long pedidoId =
                ProcesoVentaVariables.obtenerPedidoId(execution);

        preparacionPedidoService
                .marcarListoParaRetiro(pedidoId);
    }
}
