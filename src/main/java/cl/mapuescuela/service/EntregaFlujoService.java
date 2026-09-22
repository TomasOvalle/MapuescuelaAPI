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
public class EntregaFlujoService {
    private final PedidoRepository pedidoRepository;
    private final ProcesoVentaService procesoVentaService;

    public EntregaFlujoService(
            PedidoRepository pedidoRepository,
            ProcesoVentaService procesoVentaService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.procesoVentaService = procesoVentaService;
    }

    public void confirmarEntrega(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado: " + pedidoId
                        )
                );

        if (pedido.getEstado() != EstadoPedido.ENVIADO) {
            throw new BusinessRuleException(
                    "El pedido debe estar en estado ENVIADO"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia Flowable asociada"
            );
        }

        procesoVentaService.completarConfirmarEntrega(
                pedido.getProcessInstanceId()
        );
    }

    public void registrarRetiro(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado: " + pedidoId
                        )
                );

        if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RETIRO) {
            throw new BusinessRuleException(
                    "El pedido debe estar en estado LISTO_PARA_RETIRO"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia Flowable asociada"
            );
        }

        procesoVentaService.completarRegistroRetiro(
                pedido.getProcessInstanceId()
        );
    }
}
