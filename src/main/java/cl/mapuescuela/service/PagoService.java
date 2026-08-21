package cl.mapuescuela.service;

import cl.mapuescuela.dto.pago.RevisionPagoRequest;
import cl.mapuescuela.dto.pago.RevisionPagoResponse;
import cl.mapuescuela.entity.ComprobantePago;
import cl.mapuescuela.entity.DecisionPago;
import cl.mapuescuela.entity.DetallePedido;
import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.EstadoProducto;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.entity.Producto;
import cl.mapuescuela.repository.ComprobantePagoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PagoService {
    private final PedidoRepository pedidoRepository;
    private final ComprobantePagoRepository comprobantePagoRepository;

    public PagoService(
            PedidoRepository pedidoRepository,
            ComprobantePagoRepository comprobantePagoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.comprobantePagoRepository = comprobantePagoRepository;
    }

    public RevisionPagoResponse aprobarPago(
            Long pedidoId,
            RevisionPagoRequest request
    ) {
        Pedido pedido = obtenerPedido(pedidoId);
        ComprobantePago comprobante = obtenerComprobantePorPedido(pedidoId);

        validarPedidoEnRevision(pedido);
        validarComprobantePendiente(comprobante);

        descontarStock(pedido);

        comprobante.setDecision(DecisionPago.APROBADO);
        comprobante.setObservacion(request.observacion());

        pedido.setEstado(EstadoPedido.PAGO_APROBADO);

        return toResponse(pedido, comprobante);
    }

    public RevisionPagoResponse rechazarPago(
            Long pedidoId,
            RevisionPagoRequest request
    ) {
        Pedido pedido = obtenerPedido(pedidoId);
        ComprobantePago comprobante = obtenerComprobantePorPedido(pedidoId);

        validarPedidoEnRevision(pedido);
        validarComprobantePendiente(comprobante);

        comprobante.setDecision(DecisionPago.RECHAZADO);
        comprobante.setObservacion(request.observacion());

        pedido.setEstado(EstadoPedido.PAGO_RECHAZADO);

        return toResponse(pedido, comprobante);
    }

    private Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado: " + pedidoId)
                );
    }

    private ComprobantePago obtenerComprobantePorPedido(Long pedidoId) {
        return comprobantePagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe comprobante para el pedido: " + pedidoId
                        )
                );
    }

    private void validarPedidoEnRevision(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PAGO_EN_REVISION) {
            throw new RuntimeException(
                    "Solo se puede revisar el pago de un pedido en estado PAGO_EN_REVISION"
            );
        }
    }

    private void validarComprobantePendiente(ComprobantePago comprobante) {
        if (comprobante.getDecision() != DecisionPago.PENDIENTE) {
            throw new RuntimeException(
                    "El comprobante ya fue revisado"
            );
        }
    }

    private void descontarStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            Integer cantidad = detalle.getCantidad();

            if (producto.getStock() < cantidad) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto: " + producto.getId()
                );
            }

            int nuevoStock = producto.getStock() - cantidad;
            producto.setStock(nuevoStock);

            if (nuevoStock == 0) {
                producto.setEstado(EstadoProducto.AGOTADO);
            }
        }
    }

    private RevisionPagoResponse toResponse(
            Pedido pedido,
            ComprobantePago comprobante
    ) {
        return new RevisionPagoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                pedido.getEstado(),
                comprobante.getId(),
                comprobante.getDecision(),
                comprobante.getObservacion()
        );
    }
}
