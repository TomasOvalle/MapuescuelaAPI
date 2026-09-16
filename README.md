# MapuEscuela

Sistema de gestión de ventas para MapuEscuela.

El backend implementa servicios REST para cubrir el flujo principal del MVP: catálogo de productos, carrito de compras, checkout, generación de pedidos, comprobantes de pago, revisión del pago, actualización de inventario, preparación, retiro o despacho y finalización del pedido.

---

## Tecnologías

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Validation
- PostgreSQL
- Docker
- Flowable
- Maven

---

## Arquitectura

La aplicación utiliza una arquitectura por capas:

```text
Frontend
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
JPA / Hibernate
   ↓
PostgreSQL
```

Los controladores exponen los endpoints REST, los servicios contienen la lógica de negocio, los repositorios administran el acceso a datos y las entidades representan el modelo persistente.

---

# API REST

## URL base

Durante el desarrollo local, el backend se ejecuta en:

```text
http://localhost:8080
```

La URL base de la API es:

```text
http://localhost:8080/api
```

Ejemplos:

```text
http://localhost:8080/api/productos
http://localhost:8080/api/carritos
http://localhost:8080/api/pedidos
http://localhost:8080/api/despachos
```

---

# Clientes

Actualmente no existe una entidad ni un endpoint independiente `/api/clientes`.

Los datos del cliente se registran al realizar el checkout del carrito o al crear un pedido directamente.

El flujo recomendado para el frontend es realizar el checkout del carrito:

```http
POST /api/carritos/{carritoId}/checkout
```

## Body

```json
{
  "nombreCliente": "Pablo Pérez",
  "emailCliente": "pablo@example.com",
  "telefonoCliente": "+56912345678",
  "direccionDespacho": "Av. Siempre Viva 124",
  "modalidadEntrega": "DESPACHO"
}
```

Campos esperados:

| Campo | Tipo | Obligatorio | Validación |
|---|---|---:|---|
| `nombreCliente` | String | Sí | Máximo 120 caracteres |
| `emailCliente` | String | Sí | Email válido, máximo 150 |
| `telefonoCliente` | String | Sí | Máximo 30 caracteres |
| `direccionDespacho` | String | No | Máximo 300 caracteres |
| `modalidadEntrega` | Enum | Sí | `RETIRO` o `DESPACHO` |

El checkout utiliza automáticamente los productos almacenados en el carrito.

---

# Productos

Permiten administrar el catálogo de productos disponibles.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/productos` | Crear producto |
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Buscar producto por ID |
| GET | `/api/productos/disponibles` | Listar productos activos con stock |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

## Crear producto

```http
POST /api/productos
```

### Body

```json
{
  "nombre": "Harry Potter",
  "descripcion": "Libro usado en buen estado",
  "categoria": "Libros",
  "precio": 5000,
  "fotoUrl": "https://ejemplo.com/harry.jpg",
  "stock": 3
}
```

### Respuesta esperada

```http
201 Created
```

Ejemplo:

```json
{
  "id": 4,
  "nombre": "Harry Potter",
  "descripcion": "Libro usado en buen estado",
  "categoria": "Libros",
  "precio": 5000.00,
  "fotoUrl": "https://ejemplo.com/harry.jpg",
  "stock": 3,
  "estado": "ACTIVO",
  "version": 0
}
```

---

# Carrito de compras

Permite crear un carrito, agregar productos, modificar cantidades, eliminar productos y convertirlo en un pedido.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/carritos` | Crear carrito vacío |
| GET | `/api/carritos` | Listar carritos |
| GET | `/api/carritos/{id}` | Consultar carrito |
| GET | `/api/carritos/estado/{estado}` | Listar por estado |
| POST | `/api/carritos/{id}/items` | Agregar producto |
| PUT | `/api/carritos/{id}/items/{productoId}` | Actualizar cantidad |
| DELETE | `/api/carritos/{id}/items/{productoId}` | Eliminar producto |
| DELETE | `/api/carritos/{id}/items` | Vaciar carrito |
| POST | `/api/carritos/{id}/checkout` | Convertir carrito en pedido |

Estados:

```text
ACTIVO
CONVERTIDO_EN_PEDIDO
CANCELADO
```

## Crear carrito

```http
POST /api/carritos
```

No requiere body.

### Respuesta esperada

```http
201 Created
```

```json
{
  "id": 1,
  "codigo": "CAR-AB12CD34",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-09-16T12:00:00",
  "total": 0,
  "items": []
}
```

---

## Agregar producto al carrito

```http
POST /api/carritos/{carritoId}/items
```

### Body

```json
{
  "productoId": 4,
  "cantidad": 2
}
```

### Respuesta esperada

```http
200 OK
```

```json
{
  "id": 1,
  "codigo": "CAR-AB12CD34",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-09-16T12:00:00",
  "total": 2000.00,
  "items": [
    {
      "id": 1,
      "productoId": 4,
      "nombreProducto": "Producto ejemplo",
      "cantidad": 2,
      "precioUnitario": 1000.00,
      "subtotal": 2000.00
    }
  ]
}
```

---

## Actualizar cantidad

```http
PUT /api/carritos/{carritoId}/items/{productoId}
```

### Body

```json
{
  "productoId": 4,
  "cantidad": 3
}
```

`cantidad` debe ser como mínimo `1`.

### Respuesta esperada

```http
200 OK
```

Devuelve el carrito completo con el total recalculado.

---

## Checkout del carrito

```http
POST /api/carritos/{carritoId}/checkout
```

### Body

```json
{
  "nombreCliente": "Pablo Pérez",
  "emailCliente": "pablo@example.com",
  "telefonoCliente": "+56912345678",
  "direccionDespacho": "Av. Siempre Viva 124",
  "modalidadEntrega": "DESPACHO"
}
```

### Comportamiento

```text
Carrito ACTIVO
      ↓
Checkout
      ↓
Pedido PENDIENTE_PAGO
      ↓
Carrito CONVERTIDO_EN_PEDIDO
```

### Respuesta esperada

```http
200 OK
```

Ejemplo:

```json
{
  "id": 5,
  "codigo": "PED-A1B2C3D4",
  "nombreCliente": "Pablo Pérez",
  "emailCliente": "pablo@example.com",
  "telefonoCliente": "+56912345678",
  "direccionDespacho": "Av. Siempre Viva 124",
  "modalidadEntrega": "DESPACHO",
  "estado": "PENDIENTE_PAGO",
  "total": 2000.00,
  "fechaCreacion": "2026-09-16T12:10:00",
  "detalles": [
    {
      "id": 8,
      "productoId": 4,
      "nombreProducto": "Producto ejemplo",
      "cantidad": 2,
      "precioUnitario": 1000.00,
      "subtotal": 2000.00
    }
  ]
}
```

---

# Pedidos

Permiten administrar los pedidos.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/pedidos` | Crear pedido directamente |
| GET | `/api/pedidos` | Listar pedidos |
| GET | `/api/pedidos/{id}` | Buscar por ID |
| GET | `/api/pedidos/codigo/{codigo}` | Buscar por código |
| GET | `/api/pedidos/estado/{estado}` | Listar por estado |
| PUT | `/api/pedidos/{id}` | Actualizar pedido |
| DELETE | `/api/pedidos/{id}` | Eliminar pedido |

> Para el flujo normal del MVP se recomienda crear el pedido mediante el checkout del carrito.

## Crear pedido directamente

```http
POST /api/pedidos
```

### Body

```json
{
  "nombreCliente": "Pablo Pérez",
  "emailCliente": "pablo@example.com",
  "telefonoCliente": "+56912345678",
  "direccionDespacho": "Av. Siempre Viva 124",
  "modalidadEntrega": "DESPACHO",
  "detalles": [
    {
      "productoId": 4,
      "cantidad": 2
    }
  ]
}
```

### Respuesta esperada

```http
201 Created
```

El pedido queda inicialmente en:

```text
PENDIENTE_PAGO
```

Ejemplo:

```json
{
  "id": 5,
  "codigo": "PED-A1B2C3D4",
  "nombreCliente": "Pablo Pérez",
  "emailCliente": "pablo@example.com",
  "telefonoCliente": "+56912345678",
  "direccionDespacho": "Av. Siempre Viva 124",
  "modalidadEntrega": "DESPACHO",
  "estado": "PENDIENTE_PAGO",
  "total": 2000.00,
  "fechaCreacion": "2026-09-16T12:10:00",
  "detalles": [
    {
      "id": 8,
      "productoId": 4,
      "nombreProducto": "Producto ejemplo",
      "cantidad": 2,
      "precioUnitario": 1000.00,
      "subtotal": 2000.00
    }
  ]
}
```

---

# Comprobante de pago

Permite registrar y consultar el comprobante asociado a un pedido.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/pedidos/{pedidoId}/comprobante` | Registrar comprobante |
| GET | `/api/pedidos/{pedidoId}/comprobante` | Consultar comprobante |

## Registrar comprobante

```http
POST /api/pedidos/{pedidoId}/comprobante
```

El pedido debe encontrarse en:

```text
PENDIENTE_PAGO
```

### Body

```json
{
  "nombreArchivo": "comprobante-pedido-5.jpg",
  "rutaArchivo": "https://ejemplo.com/comprobantes/comprobante-pedido-5.jpg",
  "observacion": "Transferencia realizada por el cliente"
}
```

Campos:

| Campo | Obligatorio | Restricción |
|---|---:|---|
| `nombreArchivo` | Sí | Máximo 255 caracteres |
| `rutaArchivo` | Sí | Máximo 500 caracteres |
| `observacion` | No | Máximo 1000 caracteres |

### Respuesta esperada

```http
201 Created
```

```json
{
  "id": 1,
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "nombreArchivo": "comprobante-pedido-5.jpg",
  "rutaArchivo": "https://ejemplo.com/comprobantes/comprobante-pedido-5.jpg",
  "fechaCarga": "2026-09-16T12:20:00",
  "decision": "PENDIENTE",
  "observacion": "Transferencia realizada por el cliente"
}
```

Después de esta operación:

```text
Pedido:
PENDIENTE_PAGO → PAGO_EN_REVISION

Comprobante:
PENDIENTE
```

---

# Revisión del pago

Permite aprobar o rechazar el comprobante.

## Aprobar pago

```http
POST /api/pedidos/{pedidoId}/pago/aprobar
```

### Body

```json
{
  "observacion": "Comprobante revisado y aprobado correctamente"
}
```

`observacion` es opcional y permite hasta 1000 caracteres.

### Respuesta esperada

```http
200 OK
```

```json
{
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "estadoPedido": "PAGO_APROBADO",
  "comprobanteId": 1,
  "decisionPago": "APROBADO",
  "observacion": "Comprobante revisado y aprobado correctamente"
}
```

También se descuenta el stock correspondiente a los productos del pedido.

---

## Rechazar pago

```http
POST /api/pedidos/{pedidoId}/pago/rechazar
```

### Body

```json
{
  "observacion": "El monto transferido no corresponde al total del pedido"
}
```

### Respuesta esperada

```http
200 OK
```

```json
{
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "estadoPedido": "PAGO_RECHAZADO",
  "comprobanteId": 1,
  "decisionPago": "RECHAZADO",
  "observacion": "El monto transferido no corresponde al total del pedido"
}
```

Al rechazar el pago no se descuenta stock.

---

# Preparación del pedido

## Iniciar preparación

```http
POST /api/pedidos/{pedidoId}/preparacion/iniciar
```

No requiere body.

Solo puede ejecutarse cuando el pedido se encuentra en:

```text
PAGO_APROBADO
```

### Respuesta esperada

```http
200 OK
```

```json
{
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "estadoAnterior": "PAGO_APROBADO",
  "estadoActual": "EN_PREPARACION",
  "modalidadEntrega": "DESPACHO",
  "mensaje": "Pedido en preparación"
}
```

---

# Retiro

Para pedidos con:

```text
modalidadEntrega = RETIRO
```

se utiliza:

```http
POST /api/pedidos/{pedidoId}/entrega/listo-retiro
```

No requiere body.

### Transición

```text
EN_PREPARACION
      ↓
LISTO_PARA_RETIRO
```

### Respuesta esperada

```http
200 OK
```

```json
{
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "estadoAnterior": "EN_PREPARACION",
  "estadoActual": "LISTO_PARA_RETIRO",
  "modalidadEntrega": "RETIRO",
  "mensaje": "Pedido listo para retiro"
}
```

---

# Despachos

Para pedidos con modalidad `DESPACHO` se registran además los datos logísticos.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/despachos` | Listar despachos |
| GET | `/api/despachos/{id}` | Consultar despacho |
| GET | `/api/despachos/pedido/{pedidoId}` | Consultar por pedido |
| POST | `/api/despachos` | Registrar despacho |
| PUT | `/api/despachos/{id}` | Actualizar despacho |

## Registrar despacho

```http
POST /api/despachos
```

El pedido debe cumplir:

```text
modalidadEntrega = DESPACHO
estado = EN_PREPARACION
```

### Body para COURIER

```json
{
  "pedidoId": 5,
  "tipo": "COURIER",
  "empresaTransporte": "Chilexpress",
  "numeroSeguimiento": "CHX-123456789",
  "fechaEnvio": "2026-09-16"
}
```

Para:

```text
tipo = COURIER
```

son obligatorios:

```text
empresaTransporte
numeroSeguimiento
```

### Body para VOLUNTARIO

```json
{
  "pedidoId": 5,
  "tipo": "VOLUNTARIO",
  "empresaTransporte": null,
  "numeroSeguimiento": null,
  "fechaEnvio": "2026-09-16"
}
```

Valores permitidos para `tipo`:

```text
COURIER
VOLUNTARIO
```

### Respuesta esperada

```http
201 Created
```

```json
{
  "id": 1,
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "tipo": "COURIER",
  "empresaTransporte": "Chilexpress",
  "numeroSeguimiento": "CHX-123456789",
  "fechaEnvio": "2026-09-16"
}
```

Al registrar el despacho:

```text
EN_PREPARACION
      ↓
ENVIADO
```

> Para el flujo actual del frontend se recomienda utilizar `POST /api/despachos` para los pedidos con modalidad `DESPACHO`, ya que además de actualizar el estado permite persistir empresa, seguimiento, tipo y fecha de envío.

---

# Finalizar pedido

```http
POST /api/pedidos/{pedidoId}/finalizar
```

No requiere body.

Se permite desde:

```text
LISTO_PARA_RETIRO
```

o:

```text
ENVIADO
```

### Respuesta esperada

```http
200 OK
```

```json
{
  "pedidoId": 5,
  "codigoPedido": "PED-A1B2C3D4",
  "estadoAnterior": "ENVIADO",
  "estadoActual": "FINALIZADO",
  "modalidadEntrega": "DESPACHO",
  "mensaje": "Pedido finalizado"
}
```

---

# Estados principales del pedido

```text
PENDIENTE_PAGO
PAGO_EN_REVISION
PAGO_RECHAZADO
PAGO_APROBADO
EN_PREPARACION
LISTO_PARA_RETIRO
ENVIADO
FINALIZADO
CANCELADO
```

---

# Flujo principal implementado

```text
Catálogo de productos
        ↓
Carrito ACTIVO
        ↓
Agregar productos
        ↓
Checkout
        ↓
Pedido PENDIENTE_PAGO
        ↓
Registrar comprobante
        ↓
PAGO_EN_REVISION
        ↓
   ┌────┴─────┐
   ↓          ↓
APROBADO   RECHAZADO
   ↓
PAGO_APROBADO
   ↓
Descuento de stock
   ↓
EN_PREPARACION
   ↓
 ┌──────────────┴──────────────┐
 ↓                             ↓
RETIRO                      DESPACHO
 ↓                             ↓
LISTO_PARA_RETIRO       Registrar despacho
                               ↓
                            ENVIADO
 └──────────────┬──────────────┘
                ↓
           FINALIZADO
```

---

# Manejo global de errores HTTP

La API implementa manejo centralizado de excepciones mediante `@RestControllerAdvice`.

Los códigos principales utilizados son:

| Código | Significado | Ejemplo |
|---:|---|---|
| `400 Bad Request` | Request o validación incorrecta | email inválido, cantidad menor a 1, enum inválido |
| `404 Not Found` | Recurso inexistente | producto, carrito, pedido, comprobante o despacho inexistente |
| `409 Conflict` | Regla de negocio incumplida | stock insuficiente, estado incorrecto, despacho duplicado |
| `500 Internal Server Error` | Error inesperado | error no controlado por la aplicación |

---

## Formato estándar de error

Los errores tienen la siguiente estructura:

```json
{
  "timestamp": "2026-09-16T12:30:15.123",
  "status": 409,
  "error": "Conflict",
  "message": "Descripción del error",
  "path": "/api/recurso",
  "validationErrors": null
}
```

---

## Ejemplo: recurso inexistente

```http
404 Not Found
```

```json
{
  "timestamp": "2026-09-16T12:30:15.123",
  "status": 404,
  "error": "Not Found",
  "message": "Pedido no encontrado: 999",
  "path": "/api/pedidos/999",
  "validationErrors": null
}
```

---

## Ejemplo: regla de negocio

Por ejemplo, intentar registrar un despacho para un pedido que no está en preparación:

```http
409 Conflict
```

```json
{
  "timestamp": "2026-09-16T12:30:15.123",
  "status": 409,
  "error": "Conflict",
  "message": "Solo se puede crear un despacho para pedidos en estado EN_PREPARACION",
  "path": "/api/despachos",
  "validationErrors": null
}
```

Otros casos que pueden producir `409 Conflict`:

```text
Stock insuficiente.
Producto no activo.
Carrito ya convertido en pedido.
Pedido en estado incorrecto.
Pedido ya tiene comprobante.
Comprobante ya revisado.
Pedido ya tiene despacho.
Pedido de modalidad RETIRO intentando registrar despacho.
COURIER sin empresa de transporte.
COURIER sin número de seguimiento.
```

---

## Ejemplo: errores de validación

```http
400 Bad Request
```

Ejemplo de body incorrecto:

```json
{
  "nombreCliente": "",
  "emailCliente": "",
  "telefonoCliente": "",
  "modalidadEntrega": "DESPACHO",
  "detalles": []
}
```

Respuesta:

```json
{
  "timestamp": "2026-09-16T12:30:15.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en la solicitud",
  "path": "/api/pedidos",
  "validationErrors": {
    "nombreCliente": "El nombre del cliente es obligatorio",
    "emailCliente": "El email del cliente es obligatorio",
    "telefonoCliente": "El teléfono del cliente es obligatorio",
    "detalles": "El pedido debe contener al menos un producto"
  }
}
```

---

## Ejemplo: enum inválido

Por ejemplo:

```json
{
  "modalidadEntrega": "ENVIO"
}
```

`ENVIO` no existe. Los valores permitidos son:

```text
RETIRO
DESPACHO
```

La API responde:

```http
400 Bad Request
```

con un formato similar a:

```json
{
  "timestamp": "2026-09-16T12:30:15.123",
  "status": 400,
  "error": "Bad Request",
  "message": "El cuerpo de la solicitud no es válido o contiene valores incorrectos",
  "path": "/api/pedidos",
  "validationErrors": null
}
```

---

# Resumen de endpoints para integración frontend

| Acción | Método | Ruta |
|---|---|---|
| Consultar productos disponibles | GET | `/api/productos/disponibles` |
| Crear carrito | POST | `/api/carritos` |
| Consultar carrito | GET | `/api/carritos/{id}` |
| Agregar producto | POST | `/api/carritos/{id}/items` |
| Modificar cantidad | PUT | `/api/carritos/{id}/items/{productoId}` |
| Eliminar producto | DELETE | `/api/carritos/{id}/items/{productoId}` |
| Realizar checkout y registrar datos cliente | POST | `/api/carritos/{id}/checkout` |
| Consultar pedido | GET | `/api/pedidos/{id}` |
| Registrar comprobante | POST | `/api/pedidos/{id}/comprobante` |
| Consultar comprobante | GET | `/api/pedidos/{id}/comprobante` |
| Aprobar pago | POST | `/api/pedidos/{id}/pago/aprobar` |
| Rechazar pago | POST | `/api/pedidos/{id}/pago/rechazar` |
| Iniciar preparación | POST | `/api/pedidos/{id}/preparacion/iniciar` |
| Marcar listo para retiro | POST | `/api/pedidos/{id}/entrega/listo-retiro` |
| Registrar despacho | POST | `/api/despachos` |
| Listar despachos | GET | `/api/despachos` |
| Consultar despacho por pedido | GET | `/api/despachos/pedido/{pedidoId}` |
| Actualizar despacho | PUT | `/api/despachos/{id}` |
| Finalizar pedido | POST | `/api/pedidos/{id}/finalizar` |

---

# Base de datos

PostgreSQL se ejecuta mediante Docker Compose.

Para iniciar la base de datos:

```bash
docker compose up -d
```

---

# Verificación en PostgreSQL

Se puede verificar la persistencia ejecutando:

```sql
SELECT * FROM productos;
SELECT * FROM carritos;
SELECT * FROM items_carrito;
SELECT * FROM pedidos;
SELECT * FROM detalles_pedido;
SELECT * FROM comprobantes_pago;
SELECT * FROM despachos;
```

El flujo permite comprobar la persistencia e integración entre:

```text
Producto
   ↓
ItemCarrito
   ↓
Carrito
   ↓
Pedido
   ↓
DetallePedido
   ↓
ComprobantePago
   ↓
Despacho
```

---

# CORS

La API utiliza una configuración CORS global aplicada a:

```text
/api/**
```

Por lo tanto, los controladores no necesitan declarar `@CrossOrigin` individualmente.

Durante desarrollo deben configurarse explícitamente los orígenes correspondientes al frontend.

---

# BPMN

Los diagramas BPMN del proceso se encuentran en:

```text
docs/bpmn/
```

El proceso modela el flujo principal de venta de MapuEscuela y sirve como base para la integración con Flowable.