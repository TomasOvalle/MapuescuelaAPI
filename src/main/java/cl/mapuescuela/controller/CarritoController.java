package cl.mapuescuela.controller;

import cl.mapuescuela.dto.carrito.CarritoResponse;
import cl.mapuescuela.dto.carrito.CheckoutCarritoRequest;
import cl.mapuescuela.dto.carrito.ItemCarritoRequest;
import cl.mapuescuela.dto.pedido.PedidoResponse;
import cl.mapuescuela.entity.EstadoCarrito;
import cl.mapuescuela.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoResponse crearCarrito() {
        return carritoService.crearCarrito();
    }

    @GetMapping
    public List<CarritoResponse> listar() {
        return carritoService.listar();
    }

    @GetMapping("/{id}")
    public CarritoResponse buscarPorId(@PathVariable Long id) {
        return carritoService.buscarPorId(id);
    }

    @GetMapping("/estado/{estado}")
    public List<CarritoResponse> listarPorEstado(
            @PathVariable EstadoCarrito estado
    ) {
        return carritoService.listarPorEstado(estado);
    }

    @PostMapping("/{id}/items")
    public CarritoResponse agregarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemCarritoRequest request
    ) {
        return carritoService.agregarItem(id, request);
    }

    @PutMapping("/{id}/items/{productoId}")
    public CarritoResponse actualizarCantidad(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @Valid @RequestBody ItemCarritoRequest request
    ) {
        return carritoService.actualizarCantidad(
                id,
                productoId,
                request
        );
    }

    @DeleteMapping("/{id}/items/{productoId}")
    public CarritoResponse eliminarItem(
            @PathVariable Long id,
            @PathVariable Long productoId
    ) {
        return carritoService.eliminarItem(id, productoId);
    }

    @DeleteMapping("/{id}/items")
    public CarritoResponse vaciarCarrito(@PathVariable Long id) {
        return carritoService.vaciarCarrito(id);
    }

    @PostMapping("/{id}/checkout")
    public PedidoResponse checkout(
            @PathVariable Long id,
            @Valid @RequestBody CheckoutCarritoRequest request
    ) {
        return carritoService.checkout(id, request);
    }
}
