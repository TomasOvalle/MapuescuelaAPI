package cl.mapuescuela.service;

import cl.mapuescuela.dto.pedido.CambioEstadoPedidoResponse;
import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.ModalidadEntrega;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PreparacionPedidoService {
    private final PedidoRepository pedidoRepository;

    public PreparacionPedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public CambioEstadoPedidoResponse iniciarPreparacion(Long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);

        EstadoPedido estadoAnterior = pedido.getEstado();

        if (estadoAnterior != EstadoPedido.PAGO_APROBADO) {
            throw new RuntimeException(
                    "Solo se puede iniciar preparación de un pedido con pago aprobado"
            );
        }

        pedido.setEstado(EstadoPedido.EN_PREPARACION);

        return new CambioEstadoPedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                estadoAnterior,
                pedido.getEstado(),
                pedido.getModalidadEntrega(),
                "Pedido en preparación"
        );
    }

    public CambioEstadoPedidoResponse marcarListoParaRetiro(Long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);

        EstadoPedido estadoAnterior = pedido.getEstado();

        if (estadoAnterior != EstadoPedido.EN_PREPARACION) {
            throw new RuntimeException(
                    "Solo se puede marcar como listo para retiro un pedido en preparación"
            );
        }

        if (pedido.getModalidadEntrega() != ModalidadEntrega.RETIRO) {
            throw new RuntimeException(
                    "Solo los pedidos con modalidad RETIRO pueden marcarse como LISTO_PARA_RETIRO"
            );
        }

        pedido.setEstado(EstadoPedido.LISTO_PARA_RETIRO);

        return new CambioEstadoPedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                estadoAnterior,
                pedido.getEstado(),
                pedido.getModalidadEntrega(),
                "Pedido listo para retiro"
        );
    }

    public CambioEstadoPedidoResponse marcarEnviado(Long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);

        EstadoPedido estadoAnterior = pedido.getEstado();

        if (estadoAnterior != EstadoPedido.EN_PREPARACION) {
            throw new RuntimeException(
                    "Solo se puede marcar como enviado un pedido en preparación"
            );
        }

        if (pedido.getModalidadEntrega() != ModalidadEntrega.DESPACHO) {
            throw new RuntimeException(
                    "Solo los pedidos con modalidad DESPACHO pueden marcarse como ENVIADO"
            );
        }

        pedido.setEstado(EstadoPedido.ENVIADO);

        return new CambioEstadoPedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                estadoAnterior,
                pedido.getEstado(),
                pedido.getModalidadEntrega(),
                "Pedido enviado"
        );
    }

    public CambioEstadoPedidoResponse finalizarPedido(Long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);

        EstadoPedido estadoAnterior = pedido.getEstado();

        if (
                estadoAnterior != EstadoPedido.LISTO_PARA_RETIRO
                        && estadoAnterior != EstadoPedido.ENVIADO
        ) {
            throw new RuntimeException(
                    "Solo se puede finalizar un pedido listo para retiro o enviado"
            );
        }

        pedido.setEstado(EstadoPedido.FINALIZADO);

        return new CambioEstadoPedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                estadoAnterior,
                pedido.getEstado(),
                pedido.getModalidadEntrega(),
                "Pedido finalizado"
        );
    }

    private Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado: " + pedidoId)
                );
    }
}
