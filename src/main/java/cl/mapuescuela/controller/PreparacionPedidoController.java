package cl.mapuescuela.controller;

import cl.mapuescuela.dto.pedido.CambioEstadoPedidoResponse;
import cl.mapuescuela.service.PreparacionPedidoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}")
public class PreparacionPedidoController {
    private final PreparacionPedidoService preparacionPedidoService;

    public PreparacionPedidoController(
            PreparacionPedidoService preparacionPedidoService
    ) {
        this.preparacionPedidoService = preparacionPedidoService;
    }

    @PostMapping("/preparacion/iniciar")
    public CambioEstadoPedidoResponse iniciarPreparacion(
            @PathVariable Long pedidoId
    ) {
        return preparacionPedidoService.iniciarPreparacion(pedidoId);
    }

    @PostMapping("/entrega/listo-retiro")
    public CambioEstadoPedidoResponse marcarListoParaRetiro(
            @PathVariable Long pedidoId
    ) {
        return preparacionPedidoService.marcarListoParaRetiro(pedidoId);
    }

    @PostMapping("/entrega/enviar")
    public CambioEstadoPedidoResponse marcarEnviado(
            @PathVariable Long pedidoId
    ) {
        return preparacionPedidoService.marcarEnviado(pedidoId);
    }

    @PostMapping("/finalizar")
    public CambioEstadoPedidoResponse finalizarPedido(
            @PathVariable Long pedidoId
    ) {
        return preparacionPedidoService.finalizarPedido(pedidoId);
    }
}
