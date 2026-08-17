package cl.mapuescuela.service;

import cl.mapuescuela.dto.producto.ProductoRequest;
import cl.mapuescuela.dto.producto.ProductoResponse;
import cl.mapuescuela.entity.Producto;
import cl.mapuescuela.entity.EstadoProducto;
import cl.mapuescuela.exception.ProductoNotFoundException;
import cl.mapuescuela.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoResponse crear(ProductoRequest request) {

        Producto producto = new Producto(
                request.nombre(),
                request.descripcion(),
                request.categoria(),
                request.precio(),
                request.fotoUrl(),
                request.stock()
        );

        Producto guardado = productoRepository.save(producto);

        return toResponse(guardado);
    }

    public ProductoResponse actualizar(
            Long id,
            ProductoRequest request
    ) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ProductoNotFoundException(id)
                );
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setFotoUrl(request.fotoUrl());
        producto.setStock(request.stock());

        Producto actualizado = productoRepository.save(producto);

        return toResponse(actualizado);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ProductoNotFoundException(id)
                );
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarDisponibles() {
        return productoRepository
                .findByEstadoAndStockGreaterThan(
                        EstadoProducto.ACTIVO,
                        0
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ProductoNotFoundException(id)
                );
        return toResponse(producto);
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getFotoUrl(),
                producto.getStock(),
                producto.getEstado(),
                producto.getVersion()
        );
    }
}
