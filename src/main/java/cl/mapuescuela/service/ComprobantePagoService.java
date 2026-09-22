package cl.mapuescuela.service;

import cl.mapuescuela.dto.comprobante.ComprobantePagoRequest;
import cl.mapuescuela.dto.comprobante.ComprobantePagoResponse;
import cl.mapuescuela.entity.ComprobantePago;
import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.entity.DecisionPago;
import cl.mapuescuela.exception.BusinessRuleException;
import cl.mapuescuela.exception.ResourceNotFoundException;
import cl.mapuescuela.repository.ComprobantePagoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.process.ProcesoVentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProcesoVentaService procesoVentaService;

    public ComprobantePagoService(
            ComprobantePagoRepository comprobantePagoRepository,
            PedidoRepository pedidoRepository,
            ProcesoVentaService procesoVentaService
    ) {
        this.comprobantePagoRepository = comprobantePagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.procesoVentaService = procesoVentaService;
    }

    public ComprobantePagoResponse registrarComprobante (
            Long pedidoId,
            ComprobantePagoRequest request
    ) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido no encontrado: " + pedidoId)
                );

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
            throw new BusinessRuleException(
                    "Solo se puede adjuntar comprobante a un pedido pendiente de pago"
            );
        }

        if (comprobantePagoRepository.existsByPedidoId(pedidoId)) {
            throw new BusinessRuleException(
                    "El pedido ya tiene un comprobante asociado"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia de proceso Flowable asociada"
            );
        }

        ComprobantePago comprobante = new ComprobantePago();

        comprobante.setPedido(pedido);
        comprobante.setNombreArchivo(request.nombreArchivo());
        comprobante.setRutaArchivo(request.rutaArchivo());
        comprobante.setObservacion(request.observacion());
        comprobante.setDecision(DecisionPago.PENDIENTE);

        pedido.setEstado(EstadoPedido.PAGO_EN_REVISION);

        ComprobantePago guardado = comprobantePagoRepository.save(comprobante);

        procesoVentaService.completarAdjuntarComprobante(
                pedido.getProcessInstanceId()
        );

        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public ComprobantePagoResponse buscarPorPedido(Long pedidoId) {
        ComprobantePago comprobante = comprobantePagoRepository
                .findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comprobante no encontrado para el pedido: " + pedidoId
                        )
                );

        return toResponse(comprobante);
    }

    @Transactional(readOnly = true)
    public ComprobantePagoResponse buscarPorId(Long id) {
        ComprobantePago comprobante = comprobantePagoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comprobante no encontrado: " + id)
                );
        return toResponse(comprobante);
    }

    private ComprobantePagoResponse toResponse(
            ComprobantePago comprobante
    ) {
        return new ComprobantePagoResponse(
                comprobante.getId(),
                comprobante.getPedido().getId(),
                comprobante.getPedido().getCodigo(),
                comprobante.getNombreArchivo(),
                comprobante.getRutaArchivo(),
                comprobante.getFechaCarga(),
                comprobante.getDecision(),
                comprobante.getObservacion()
        );
    }
}
