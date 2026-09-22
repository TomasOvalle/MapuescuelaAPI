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
public class PreparacionFlujoService {
    private final PedidoRepository pedidoRepository;
    private final ProcesoVentaService procesoVentaService;

    public PreparacionFlujoService(
            PedidoRepository pedidoRepository,
            ProcesoVentaService procesoVentaService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.procesoVentaService = procesoVentaService;
    }

    public void completarPreparacion(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado: " + pedidoId
                        )
                );

        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new BusinessRuleException(
                    "El pedido debe estar en estado EN_PREPARACION"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia Flowable asociada"
            );
        }

        procesoVentaService.completarPreparacion(
                pedido.getProcessInstanceId()
        );
    }
}
