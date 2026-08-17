package cl.mapuescuela.controller;

import cl.mapuescuela.dto.producto.ProductoRequest;
import cl.mapuescuela.dto.producto.ProductoResponse;
import cl.mapuescuela.service.ProductoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(
            @Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request
    ) {
        return productoService.actualizar(id, request);
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    @GetMapping("/disponibles")
    public List<ProductoResponse> listarDisponibles() {
        return productoService.listarDisponibles();
    }

    @GetMapping("/{id}")
    public ProductoResponse buscarPorId(
            @PathVariable Long id) {
        return productoService.buscarPorId(id);
    }
}
