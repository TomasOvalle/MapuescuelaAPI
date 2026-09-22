package cl.mapuescuela.controller;

import cl.mapuescuela.service.PreparacionFlujoService;
import cl.mapuescuela.service.EntregaFlujoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}")
public class PreparacionPedidoController {

    private final PreparacionFlujoService preparacionFlujoService;
    private final EntregaFlujoService entregaFlujoService;

    public PreparacionPedidoController(
            PreparacionFlujoService preparacionFlujoService,
            EntregaFlujoService entregaFlujoService
    ) {
        this.preparacionFlujoService = preparacionFlujoService;
        this.entregaFlujoService = entregaFlujoService;
    }

    @PostMapping("/preparacion/completar")
    public ResponseEntity<Void> completarPreparacion(
            @PathVariable Long pedidoId
    ) {
        preparacionFlujoService.completarPreparacion(pedidoId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/retiro/confirmar")
    public ResponseEntity<Void> confirmarRetiro(
            @PathVariable Long pedidoId
    ) {
        entregaFlujoService.registrarRetiro(pedidoId);

        return ResponseEntity.noContent().build();
    }
}
