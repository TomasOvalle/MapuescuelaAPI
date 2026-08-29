package cl.mapuescuela.service;

import cl.mapuescuela.dto.carrito.CarritoResponse;
import cl.mapuescuela.dto.carrito.CheckoutCarritoRequest;
import cl.mapuescuela.dto.carrito.ItemCarritoRequest;
import cl.mapuescuela.dto.carrito.ItemCarritoResponse;
import cl.mapuescuela.dto.pedido.DetallePedidoRequest;
import cl.mapuescuela.dto.pedido.PedidoRequest;
import cl.mapuescuela.dto.pedido.PedidoResponse;
import cl.mapuescuela.entity.Carrito;
import cl.mapuescuela.entity.EstadoCarrito;
import cl.mapuescuela.entity.EstadoProducto;
import cl.mapuescuela.entity.ItemCarrito;
import cl.mapuescuela.entity.Producto;
import cl.mapuescuela.exception.BusinessRuleException;
import cl.mapuescuela.exception.ResourceNotFoundException;
import cl.mapuescuela.repository.CarritoRepository;
import cl.mapuescuela.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarritoService {
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoService pedidoService;

    public CarritoService(
            CarritoRepository carritoRepository,
            ProductoRepository productoRepository,
            PedidoService pedidoService
    ) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.pedidoService = pedidoService;
    }

    public CarritoResponse crearCarrito() {
        Carrito carrito = new Carrito();
        Carrito guardado = carritoRepository.save(carrito);
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public CarritoResponse buscarPorId(Long id) {
        Carrito carrito = obtenerCarrito(id);
        return toResponse(carrito);
    }

    @Transactional(readOnly = true)
    public List<CarritoResponse> listar() {
        return carritoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CarritoResponse> listarPorEstado(EstadoCarrito estado) {
        return carritoRepository.findByEstado(estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CarritoResponse agregarItem(
            Long carritoId,
            ItemCarritoRequest request
    ) {
        Carrito carrito = obtenerCarrito(carritoId);

        validarCarritoActivo(carrito);

        Producto producto = obtenerProducto(request.productoId());

        validarProductoDisponible(producto, request.cantidad());

        ItemCarrito itemExistente = buscarItemPorProducto(
                carrito,
                request.productoId()
        );

        if (itemExistente != null) {
            int nuevaCantidad =
                    itemExistente.getCantidad() + request.cantidad();

            validarProductoDisponible(producto, nuevaCantidad);

            itemExistente.setCantidad(nuevaCantidad);
        } else {
            ItemCarrito item = new ItemCarrito();

            item.setProducto(producto);
            item.setCantidad(request.cantidad());
            item.setPrecioUnitario(producto.getPrecio());

            carrito.agregarItem(item);
        }

        return toResponse(carrito);
    }

    public CarritoResponse actualizarCantidad(
            Long carritoId,
            Long productoId,
            ItemCarritoRequest request
    ) {
        Carrito carrito = obtenerCarrito(carritoId);

        validarCarritoActivo(carrito);

        if (!productoId.equals(request.productoId())) {
            throw new BusinessRuleException(
                    "El producto de la URL no coincide con el producto del cuerpo de la solicitud"
            );
        }

        Producto producto = obtenerProducto(productoId);

        validarProductoDisponible(producto, request.cantidad());

        ItemCarrito item = buscarItemPorProducto(carrito, productoId);

        if (item == null) {
            throw new ResourceNotFoundException(
                    "El producto no existe en el carrito: " + productoId
            );
        }

        item.setCantidad(request.cantidad());
        item.setPrecioUnitario(producto.getPrecio());

        return toResponse(carrito);
    }

    public CarritoResponse eliminarItem(
            Long carritoId,
            Long productoId
    ) {
        Carrito carrito = obtenerCarrito(carritoId);

        validarCarritoActivo(carrito);

        ItemCarrito item = buscarItemPorProducto(carrito, productoId);

        if (item == null) {
            throw new ResourceNotFoundException(
                    "El producto no existe en el carrito: " + productoId
            );
        }

        carrito.eliminarItem(item);

        return toResponse(carrito);
    }

    public CarritoResponse vaciarCarrito(Long carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);

        validarCarritoActivo(carrito);

        carrito.getItems().clear();

        return toResponse(carrito);
    }

    public PedidoResponse checkout(
            Long carritoId,
            CheckoutCarritoRequest request
    ) {
        Carrito carrito = obtenerCarrito(carritoId);

        validarCarritoActivo(carrito);

        if (carrito.getItems().isEmpty()) {
            throw new BusinessRuleException(
                    "No se puede generar un pedido desde un carrito vacío"
            );
        }

        List<DetallePedidoRequest> detalles =
                carrito.getItems()
                        .stream()
                        .map(item ->
                                new DetallePedidoRequest(
                                        item.getProducto().getId(),
                                        item.getCantidad()
                                )
                        )
                        .toList();

        PedidoRequest pedidoRequest = new PedidoRequest(
                request.nombreCliente(),
                request.emailCliente(),
                request.telefonoCliente(),
                request.direccionDespacho(),
                request.modalidadEntrega(),
                detalles
        );

        PedidoResponse pedidoResponse = pedidoService.crear(pedidoRequest);

        carrito.setEstado(EstadoCarrito.CONVERTIDO_EN_PEDIDO);

        return pedidoResponse;
    }

    private Carrito obtenerCarrito(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Carrito no encontrado: " + id
                        )
                );
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto no encontrado: " + id
                        )
                );
    }

    private void validarCarritoActivo(Carrito carrito) {
        if (carrito.getEstado() != EstadoCarrito.ACTIVO) {
            throw new BusinessRuleException(
                    "El carrito no se puede modificar porque se encuentra en estado "
                            + carrito.getEstado()
            );
        }
    }

    private void validarProductoDisponible(
            Producto producto,
            Integer cantidadSolicitada
    ) {
        if (producto.getEstado() != EstadoProducto.ACTIVO) {
            throw new BusinessRuleException(
                    "El producto no está activo: " + producto.getId()
            );
        }

        if (producto.getStock() < cantidadSolicitada) {
            throw new BusinessRuleException(
                    "Stock insuficiente para el producto: "
                            + producto.getId()
            );
        }
    }

    private ItemCarrito buscarItemPorProducto(
            Carrito carrito,
            Long productoId
    ) {
        return carrito.getItems()
                .stream()
                .filter(item ->
                        item.getProducto()
                                .getId()
                                .equals(productoId)
                )
                .findFirst()
                .orElse(null);
    }

    private CarritoResponse toResponse(Carrito carrito) {
        List<ItemCarritoResponse> items =
                carrito.getItems()
                        .stream()
                        .map(item ->
                                new ItemCarritoResponse(
                                        item.getId(),
                                        item.getProducto().getId(),
                                        item.getProducto().getNombre(),
                                        item.getCantidad(),
                                        item.getPrecioUnitario(),
                                        item.getSubtotal()
                                )
                        )
                        .toList();

        return new CarritoResponse(
                carrito.getId(),
                carrito.getCodigo(),
                carrito.getEstado(),
                carrito.getFechaCreacion(),
                carrito.calcularTotal(),
                items
        );
    }
}
