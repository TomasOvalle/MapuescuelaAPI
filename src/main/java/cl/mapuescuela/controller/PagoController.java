package cl.mapuescuela.controller;

import cl.mapuescuela.dto.pago.RevisionPagoRequest;
import cl.mapuescuela.dto.pago.RevisionPagoResponse;
import cl.mapuescuela.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}/pago")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/aprobar")
    public RevisionPagoResponse aprobarPago(
            @PathVariable Long pedidoId,
            @Valid @RequestBody RevisionPagoRequest request
    ) {
        return pagoService.aprobarPago(pedidoId, request);
    }

    @PostMapping("/rechazar")
    public RevisionPagoResponse rechazarPago(
            @PathVariable Long pedidoId,
            @Valid @RequestBody RevisionPagoRequest request
    ) {
        return pagoService.rechazarPago(pedidoId, request);
    }
}
