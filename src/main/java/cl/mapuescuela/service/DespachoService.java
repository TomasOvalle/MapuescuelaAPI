package cl.mapuescuela.service;

import cl.mapuescuela.dto.despacho.DespachoRequest;
import cl.mapuescuela.dto.despacho.DespachoResponse;
import cl.mapuescuela.entity.Despacho;
import cl.mapuescuela.entity.EstadoPedido;
import cl.mapuescuela.entity.ModalidadEntrega;
import cl.mapuescuela.entity.Pedido;
import cl.mapuescuela.entity.TipoDespacho;
import cl.mapuescuela.exception.BusinessRuleException;
import cl.mapuescuela.exception.ResourceNotFoundException;
import cl.mapuescuela.repository.DespachoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.process.ProcesoVentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DespachoService {
    private final DespachoRepository despachoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProcesoVentaService procesoVentaService;

    public DespachoService(
            DespachoRepository despachoRepository,
            PedidoRepository pedidoRepository,
            ProcesoVentaService procesoVentaService
    ) {
        this.despachoRepository = despachoRepository;
        this.pedidoRepository = pedidoRepository;
        this.procesoVentaService = procesoVentaService;
    }

    public DespachoResponse crear(DespachoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado: " + request.pedidoId()
                        )
                );

        validarPedidoParaDespacho(pedido);

        validarDatosDespacho(request);

        if (despachoRepository.existsByPedidoId(pedido.getId())) {
            throw new BusinessRuleException(
                    "El pedido ya tiene un despacho registrado"
            );
        }

        if (pedido.getProcessInstanceId() == null) {
            throw new BusinessRuleException(
                    "El pedido no tiene una instancia Flowable asociada"
            );
        }

        procesoVentaService.completarRegistroDespacho(
                pedido.getProcessInstanceId()
        );

        Despacho despacho = new Despacho();

        despacho.setPedido(pedido);
        despacho.setTipo(request.tipo());
        despacho.setFechaEnvio(request.fechaEnvio());

        aplicarDatosTransporte(despacho, request);

        pedido.setEstado(EstadoPedido.ENVIADO);

        Despacho guardado = despachoRepository.save(despacho);

        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<DespachoResponse> listar() {
        return despachoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DespachoResponse buscarPorId(Long id) {

        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Despacho no encontrado: " + id
                        )
                );

        return toResponse(despacho);
    }

    @Transactional(readOnly = true)
    public DespachoResponse buscarPorPedido(Long pedidoId) {

        Despacho despacho = despachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe despacho para el pedido: " + pedidoId
                        )
                );

        return toResponse(despacho);
    }

    public DespachoResponse actualizar(
            Long id,
            DespachoRequest request
    ) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Despacho no encontrado: " + id
                        )
                );

        if (!despacho.getPedido().getId().equals(request.pedidoId())) {
            throw new BusinessRuleException(
                    "No se puede cambiar el pedido asociado al despacho"
            );
        }

        validarDatosDespacho(request);


        despacho.setTipo(request.tipo());
        despacho.setFechaEnvio(request.fechaEnvio());

        aplicarDatosTransporte(despacho, request);

        return toResponse(despacho);
    }

    private void validarPedidoParaDespacho(Pedido pedido) {

        if (pedido.getModalidadEntrega() != ModalidadEntrega.DESPACHO) {
            throw new BusinessRuleException(
                    "Solo se puede crear un despacho para pedidos con modalidad DESPACHO"
            );
        }

        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new BusinessRuleException(
                    "Solo se puede crear un despacho para pedidos en estado EN_PREPARACION"
            );
        }
    }

    private void validarDatosDespacho(DespachoRequest request) {

        if (request.tipo() == TipoDespacho.COURIER) {

            if (request.empresaTransporte() == null
                    || request.empresaTransporte().isBlank()) {

                throw new BusinessRuleException(
                        "La empresa de transporte es obligatoria para despachos tipo COURIER"
                );
            }

            if (request.numeroSeguimiento() == null
                    || request.numeroSeguimiento().isBlank()) {

                throw new BusinessRuleException(
                        "El número de seguimiento es obligatorio para despachos tipo COURIER"
                );
            }
        }
    }

    private void aplicarDatosTransporte(
            Despacho despacho,
            DespachoRequest request
    ) {

        if (request.tipo() == TipoDespacho.VOLUNTARIO) {
            despacho.setEmpresaTransporte(null);
            despacho.setNumeroSeguimiento(null);
        } else {
            despacho.setEmpresaTransporte(request.empresaTransporte());
            despacho.setNumeroSeguimiento(request.numeroSeguimiento());
        }
    }

    private DespachoResponse toResponse(Despacho despacho) {
        return new DespachoResponse(
                despacho.getId(),
                despacho.getPedido().getId(),
                despacho.getPedido().getCodigo(),
                despacho.getTipo(),
                despacho.getEmpresaTransporte(),
                despacho.getNumeroSeguimiento(),
                despacho.getFechaEnvio()
        );
    }
}
