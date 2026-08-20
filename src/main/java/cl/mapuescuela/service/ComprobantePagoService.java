package cl.mapuescuela.service;

import cl.mapuescuela.dto.comprobante.ComprobantePagoRequest;
import cl.mapuescuela.dto.comprobante.ComprobantePagoResponse;
import cl.mapuescuela.entity.ComprobantePago;
import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.repository.ComprobantePagoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;

    public ComprobantePagoService(
            ComprobantePagoRepository comprobantePagoRepository,
            PedidoRepository pedidoRepository
    ) {
        this.comprobantePagoRepository = comprobantePagoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public ComprobantePagoResponse registrarComprobante (
            Long pedidoId,
            ComprobantePagoRequest request
    ) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado: " + pedidoId)
                );

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
            throw new RuntimeException(
                    "Solo se puede adjuntar comprobante a un pedido pendiente de pago"
            );
        }

        if (comprobantePagoRepository.existsByPedidoId(pedidoId)) {
            throw new RuntimeException(
                    "El pedido ya tiene un comprobante asociado"
            );
        }

        ComprobantePago comprobante = new ComprobantePago(
                pedido,
                request.archivoUrl(),
                request.observacion()
        );

        pedido.setEstado(EstadoPedido.PAGO_EN_REVISION);

        ComprobantePago guardado = comprobantePagoRepository.save(comprobante);

        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public ComprobantePagoResponse buscarPorPedido(Long pedidoId) {
        ComprobantePago comprobante = comprobantePagoRepository
                .findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Comprobante no encontrado para el pedido: " + pedidoId
                        )
                );

        return toResponse(comprobante);
    }

    private ComprobantePagoResponse toResponse(
            ComprobantePago comprobante
    ) {
        return new ComprobantePagoResponse(
                comprobante.getId(),
                comprobante.getPedido().getId(),
                comprobante.getArchivoUrl(),
                comprobante.getObservacion(),
                comprobante.getDecision(),
                comprobante.getFechaCarga()
        );
}
