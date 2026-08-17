package cl.mapuescuela.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombreCliente;

    @Column(nullable = false, length = 150)
    private String emailCliente;

    @Column(nullable = false, length = 30)
    private String telefonoCliente;

    @Column(length = 300)
    private String direccionDespacho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadEntrega modalidadEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoPedido estado = EstadoPedido.PENDIENTE_PAGO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(unique = true, length = 64)
    private String processInstanceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    //Se cambio el constructor de privado a public para facilitar el desarrollo
    //no es incorrecto pero hay mejores formas de manejarlo
    public Pedido() {
    }

    @PrePersist
    private void prepararNuevoPedido() {
        if (codigo == null) {
            codigo = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    public void recalcularTotal() {
        total = detalles.stream().map(DetallePedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    public String getDireccionDespacho() { return direccionDespacho; }
    public void setDireccionDespacho(String direccionDespacho) { this.direccionDespacho = direccionDespacho; }
    public ModalidadEntrega getModalidadEntrega() { return modalidadEntrega; }
    public void setModalidadEntrega(ModalidadEntrega modalidadEntrega) { this.modalidadEntrega = modalidadEntrega; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public List<DetallePedido> getDetalles() { return detalles; }
}
