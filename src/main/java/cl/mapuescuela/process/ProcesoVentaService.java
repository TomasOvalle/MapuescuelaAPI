package cl.mapuescuela.process;

import cl.mapuescuela.entity.ModalidadEntrega;
import cl.mapuescuela.exception.BusinessRuleException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Value;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcesoVentaService {
    private static final String PROCESS_KEY =
            "procesoVentaMapuescuela";

    private static final String TASK_ADJUNTAR_COMPROBANTE =
            "Task_AdjuntarComprobante";

    private static final String TASK_REVISAR_COMPROBANTE =
            "Task_RevisarComprobante";

    private static final String TASK_PREPARAR_PEDIDO =
            "Task_PrepararPedido";

    private static final String TASK_REGISTRAR_DESPACHO =
            "Task_RegistrarDespacho";

    private static final String TASK_CONFIRMAR_ENTREGA =
            "Task_ConfirmarEntrega";

    private static final String TASK_REGISTRAR_RETIRO =
            "Task_RegistrarRetiro";

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final String plazoComprobante;

    public ProcesoVentaService(
            RuntimeService runtimeService,
            TaskService taskService,
            @Value("${mapuescuela.proceso.plazo-comprobante:PT24H}")
            String plazoComprobante
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.plazoComprobante = plazoComprobante;
    }

    public String iniciarProceso(
            Long pedidoId,
            String codigoPedido,
            ModalidadEntrega modalidadEntrega
    ) {

        Map<String, Object> variables = new HashMap<>();

        variables.put(
                ProcesoVentaVariables.PEDIDO_ID,
                pedidoId
        );

        variables.put(
                ProcesoVentaVariables.MODALIDAD_ENTREGA,
                modalidadEntrega.name()
        );

        variables.put(
                ProcesoVentaVariables.PLAZO_COMPROBANTE,
                plazoComprobante
        );

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        PROCESS_KEY,
                        codigoPedido,
                        variables
                );

        return processInstance.getProcessInstanceId();
    }

    public void completarAdjuntarComprobante(
            String processInstanceId
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(TASK_ADJUNTAR_COMPROBANTE)
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea para adjuntar comprobante"
            );
        }

        taskService.complete(task.getId());
    }

    public void completarRevisionPago(
            String processInstanceId,
            boolean pagoAprobado
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey("Task_RevisarComprobante")
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea de revisión de comprobante"
            );
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put(
                ProcesoVentaVariables.PAGO_APROBADO,
                pagoAprobado
        );

        taskService.complete(
                task.getId(),
                variables
        );
    }

    public void completarPreparacion(
            String processInstanceId
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(TASK_PREPARAR_PEDIDO)
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea de preparación del pedido"
            );
        }

        taskService.complete(task.getId());
    }

    public void completarRegistroDespacho(
            String processInstanceId
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(TASK_REGISTRAR_DESPACHO)
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea de registro de despacho"
            );
        }

        taskService.complete(task.getId());
    }

    public void completarConfirmarEntrega(
            String processInstanceId
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(TASK_CONFIRMAR_ENTREGA)
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea de confirmación de entrega"
            );
        }

        taskService.complete(task.getId());
    }

    public void completarRegistroRetiro(
            String processInstanceId
    ) {

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(TASK_REGISTRAR_RETIRO)
                .singleResult();

        if (task == null) {
            throw new BusinessRuleException(
                    "El proceso no tiene activa la tarea de registro de retiro"
            );
        }

        taskService.complete(task.getId());
    }
}
