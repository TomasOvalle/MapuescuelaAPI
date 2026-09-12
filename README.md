# MapuEscuela

Sistema de gestión de ventas para MapuEscuela.

## Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Docker
- Flowable
- Maven

## Arquitectura

La aplicación utiliza una arquitectura por capas:

Controller
↓
Service
↓
Repository
↓
JPA/Hibernate
↓
PostgreSQL

## API REST

### Productos

Permiten administrar el catálogo de productos disponibles.

POST   /api/productos Crear un producto
GET    /api/productos Listar todos los productos
GET    /api/productos/{id} Buscar producto por ID
GET    /api/productos/disponibles  Listar productos activos con stock 
PUT    /api/productos/{id} Actualizar producto
DELETE /api/productos/{id} Eliminar producto

### Pedidos

Permiten administrar los pedidos.

POST   /api/pedidos Crear pedido
GET    /api/pedidos Listar todos los pedidos
GET    /api/pedidos/{id} Buscar pedido por id
GET    /api/pedidos/codigo/{codigo} Buscar pedido por código
GET    /api/pedidos/estado/{estado} Listar pedidos por estado
PUT    /api/pedidos/{id} Actualizar pedido
DELETE /api/pedidos/{id} Eliminar pedido

### Carrito de compras

Permite crear un carrito, agregar productos, modificar cantidades, eliminar productos y convertir el carrito en un pedido.

POST  /api/carritos  Crear un carrito vacío 
GET  /api/carritos Listar carritos 
GET /api/carritos/{id} Consultar carrito por ID 
GET /api/carritos/estado/{estado} Listar carritos por estado 
POST /api/carritos/{id}/items Agregar producto al carrito 
PUT /api/carritos/{id}/items/{productoId}  Actualizar cantidad de un producto 
DELETE /api/carritos/{id}/items/{productoId}  Eliminar producto del carrito 
DELETE /api/carritos/{id}/items  Vaciar carrito 
POST /api/carritos/{id}/checkout Convertir carrito en pedido

### Comprobante de pago

Permite registrar y consultar el comprobante asociado a un pedido

POST /api/pedidos/{pedidoId}/comprobante Registrar comprobante de pago
GET /api/pedidos/{pedidoId}/comprobante Consultar comprobante del pedido

### Revisión del pago

Permite aprobar o rechazar el comprobante de pago. Si el pago es aprobado, se actualiza el estado del pedido y se descuenta el stock

POST  /api/pedidos/{pedidoId}/pago/aprobar Aprobar pago
POST  /api/pedidos/{pedidoId}/pago/rechazar Rechazar pago

### Preparación y entrega

Permite avanzar el pedido luego de que el pago fue aprobado

POST /api/pedidos/{pedidoId}/preparacion/iniciar Iniciar preparación del pedid
POST /api/pedidos/{pedidoId}/entrega/listo-retiro Marcar pedido como listo para retiro
POST /api/pedidos/{pedidoId}/entrega/enviar Marcar pedido como enviado
POST /api/pedidos/{pedidoId}/finalizar Finalizar pedido

Estados del carrito:

ACTIVO
CONVERTIDO_EN_PEDIDO
CANCELADO

## Flujo principal implementado

El flujo principal del MVP se encuentra parcialmente automatizado mediante servicios REST:

1. Se consulta el catálogo de productos.
2. Se crea un carrito de compras.
3. Se agregan productos al carrito.
4. Se realiza checkout del carrito.
5. El checkout genera un pedido en estado PENDIENTE_PAGO.
6. El cliente registra un comprobante de pago.
7. El pedido cambia a PAGO_EN_REVISION.
8. El pago puede ser aprobado o rechazado.
9. Si el pago es aprobado:
   - el comprobante queda en estado APROBADO;
   - el pedido queda en estado PAGO_APROBADO;
   - se descuenta el stock de los productos.
10. Luego el pedido puede pasar a preparación, retiro/despacho y finalización.

## Estados principales del pedido:

PENDIENTE_PAGO
PAGO_EN_REVISION
PAGO_RECHAZADO
PAGO_APROBADO
EN_PREPARACION
LISTO_PARA_RETIRO
ENVIADO
FINALIZADO
CANCELADO


## Base de datos

PostgreSQL se ejecuta mediante Docker Compose.

## Verificación en base de datos

Después de probar los endpoints, se puede verificar la persistencia ejecutando consultas como:

SELECT * FROM productos;
SELECT * FROM carritos;
SELECT * FROM items_carrito;
SELECT * FROM pedidos;
SELECT * FROM detalles_pedido;
SELECT * FROM comprobantes_pago;

## BPMN

Los diagramas BPMN del proceso se encuentran en:

docs/bpmn/