package cl.mapuescuela.controller;

import cl.mapuescuela.dto.comprobante.ComprobantePagoRequest;
import cl.mapuescuela.dto.comprobante.ComprobantePagoResponse;
import cl.mapuescuela.service.ComprobantePagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos/{pedidoId}/comprobante")
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    public ComprobantePagoController(
            ComprobantePagoService comprobantePagoService
    ) {
        this.comprobantePagoService = comprobantePagoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComprobantePagoResponse registrarComprobante(
            @PathVariable Long pedidoId,
            @Valid @RequestBody ComprobantePagoRequest request
    ) {
        return comprobantePagoService.registrarComprobante(
                pedidoId,
                request
        );
    }

    @GetMapping
    public ComprobantePagoResponse buscarPorPedido(
            @PathVariable Long pedidoId
    ) {
        return comprobantePagoService.buscarPorPedido(pedidoId);
    }
}
