package cl.mapuescuela.service;

import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.exception.BusinessRuleException;
import cl.mapuescuela.exception.ResourceNotFoundException;
import cl.mapuescuela.process.ProcesoVentaService;
import cl.mapuescuela.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RevisionPagoService {
    private final PedidoRepository pedidoRepository;
    private final ProcesoVentaService procesoVentaService;

    public RevisionPagoService(
            PedidoRepository pedidoRepository,
            ProcesoVentaService procesoVentaService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.procesoVentaService = procesoVentaService;
    }

    public void aprobar(Long pedidoId) {
        completarRevision(pedidoId, true);
    }

    public void rechazar(Long pedidoId) {
        completarRevision(pedidoId, false);
    }

    private void completarRevision(
            Long pedidoId,
            boolean aprobado
    ) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado: " + pedidoId
                        )
                );

        if (pedido.getEstado() != EstadoPedido.PAGO_EN_REVISION) {
            throw new BusinessRuleException(
                    "El pedido debe estar en estado PAGO_EN_REVISION"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia Flowable asociada"
            );
        }

        procesoVentaService.completarRevisionPago(
                pedido.getProcessInstanceId(),
                aprobado
        );
    }
}
