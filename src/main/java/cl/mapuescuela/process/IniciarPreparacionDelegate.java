package cl.mapuescuela.process;

import cl.mapuescuela.service.PreparacionPedidoService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("iniciarPreparacionDelegate")
public class IniciarPreparacionDelegate implements JavaDelegate {
    private final PreparacionPedidoService preparacionPedidoService;

    public IniciarPreparacionDelegate(
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
                .iniciarPreparacion(pedidoId);
    }
}
