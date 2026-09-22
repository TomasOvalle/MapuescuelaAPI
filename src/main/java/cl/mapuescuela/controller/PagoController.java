package cl.mapuescuela.controller;

import cl.mapuescuela.service.RevisionPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}/pago")
public class PagoController {

    private final RevisionPagoService revisionPagoService;

    public PagoController(
            RevisionPagoService revisionPagoService
    ) {
        this.revisionPagoService = revisionPagoService;
    }

    @PostMapping("/aprobar")
    public ResponseEntity<Void> aprobarPago(
            @PathVariable Long pedidoId
    ) {
        revisionPagoService.aprobar(pedidoId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rechazar")
    public ResponseEntity<Void> rechazarPago(
            @PathVariable Long pedidoId
    ) {
        revisionPagoService.rechazar(pedidoId);

        return ResponseEntity.noContent().build();
    }
}
