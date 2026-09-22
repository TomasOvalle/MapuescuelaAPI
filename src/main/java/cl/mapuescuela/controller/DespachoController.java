package cl.mapuescuela.controller;

import cl.mapuescuela.dto.despacho.DespachoRequest;
import cl.mapuescuela.dto.despacho.DespachoResponse;
import cl.mapuescuela.service.DespachoService;
import cl.mapuescuela.service.EntregaFlujoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {
    private final DespachoService despachoService;
    private final EntregaFlujoService entregaFlujoService;

    public DespachoController(DespachoService despachoService, EntregaFlujoService entregaFlujoService) {
        this.despachoService = despachoService;
        this.entregaFlujoService = entregaFlujoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DespachoResponse crear(
            @Valid @RequestBody DespachoRequest request
    ) {
        return despachoService.crear(request);
    }

    @GetMapping
    public List<DespachoResponse> listar() {
        return despachoService.listar();
    }

    @GetMapping("/{id}")
    public DespachoResponse buscarPorId(
            @PathVariable Long id
    ) {
        return despachoService.buscarPorId(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public DespachoResponse buscarPorPedido(
            @PathVariable Long pedidoId
    ) {
        return despachoService.buscarPorPedido(pedidoId);
    }

    @PutMapping("/{id}")
    public DespachoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DespachoRequest request
    ) {
        return despachoService.actualizar(id, request);
    }

    @PostMapping("/pedidos/{pedidoId}/confirmar-entrega")
    public ResponseEntity<Void> confirmarEntrega(
            @PathVariable Long pedidoId
    ) {
        entregaFlujoService.confirmarEntrega(pedidoId);

        return ResponseEntity.noContent().build();
    }
}
