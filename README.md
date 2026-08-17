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

POST   /api/productos
GET    /api/productos
GET    /api/productos/{id}
PUT    /api/productos/{id}
DELETE /api/productos/{id}

### Pedidos

POST   /api/pedidos
GET    /api/pedidos
GET    /api/pedidos/{id}
PUT    /api/pedidos/{id}
DELETE /api/pedidos/{id}

## Base de datos

PostgreSQL se ejecuta mediante Docker Compose.

## BPMN

Los diagramas BPMN del proceso se encuentran en:

docs/bpmn/