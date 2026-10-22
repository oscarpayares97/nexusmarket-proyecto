# API REST: NexusMarket

Base: `http://localhost:8080/api/v1`. JSON. Autenticación con JWT en la cabecera
`Authorization: Bearer <token>` (se obtiene en `POST /auth/login`). El prefijo de la ruta define el rol
exigido por `SecurityConfig`; además cada servicio de dominio valida el rol del actor.

Los controladores solo dependen de los puertos de entrada (`application.domain.ports.in`) y
transforman con `DomainRestMapper`. Los cuerpos de entrada están en `RequestDTOs` (validados con
jakarta.validation) y los de salida en `ResponseDTOs`.

## Códigos de respuesta
| Código | Cuándo |
|---|---|
| 200 / 201 / 204 | Éxito / creado / éxito sin cuerpo |
| 400 | Datos inválidos o JSON mal formado |
| 401 | Sin token, token inválido o credenciales incorrectas |
| 403 | Rol sin permiso para la operación |
| 404 | Recurso no encontrado |
| 409 | Regla de negocio violada (stock insuficiente, estado inválido, duplicado) |

## Públicos (sin token)
| Método | Ruta | Puerto / operación |
|---|---|---|
| POST | /auth/login | PublicAccessPort.login |
| POST | /auth/register/buyer | PublicAccessPort.registerBuyer |
| GET | /public/products | consultPublishedProducts |

## BUYER (`/buyer/**`)
| Método | Ruta | Operación |
|---|---|---|
| POST | /buyer/addresses | addAddress |
| POST | /buyer/cart/items | addItemToCart |
| DELETE | /buyer/cart/items/{productId} | removeItemFromCart |
| POST | /buyer/orders/confirm | confirmOrder |
| POST | /buyer/orders/{orderId}/pay | payOrder |
| GET | /buyer/orders | consultMyOrders |
| GET | /buyer/orders/{orderId}/invoice | consultInvoice |
| GET | /buyer/orders/{orderId}/shipment | consultShipment |
| POST | /buyer/returns | requestReturn |
| GET | /buyer/returns | consultMyReturns |
| PUT | /buyer/password | changePassword |

## SELLER (`/seller/**`)
| Método | Ruta | Operación |
|---|---|---|
| POST | /seller/products | registerProduct |
| POST | /seller/products/{id}/publish | publishProduct |
| POST | /seller/products/{id}/variants | addProductVariant |
| POST | /seller/products/{id}/suspend | suspendProduct |
| POST | /seller/products/{id}/discontinue | discontinueProduct |
| POST | /seller/inventory/entries | registerInventoryEntry |
| PUT | /seller/inventory/adjustment | adjustInventory |
| PUT | /seller/inventory/status | changeInventoryStatus |
| GET | /seller/inventory/product/{productId} | consultInventory |
| GET | /seller/warehouses | consultWarehouses |
| PUT | /seller/password | changePassword |

## LOGISTICS_OPERATOR (`/logistics/**`)
| Método | Ruta | Operación |
|---|---|---|
| POST | /logistics/inventory/entries | registerInventoryEntry |
| PUT | /logistics/inventory/adjustment | adjustInventory |
| PUT | /logistics/inventory/status | changeInventoryStatus |
| GET | /logistics/inventory/product/{productId} | consultInventory |
| GET | /logistics/inventory/movements | consultInventoryMovements |
| GET | /logistics/warehouses | consultWarehouses |
| POST | /logistics/orders/{orderId}/shipment/prepare | startShipmentPreparation |
| POST | /logistics/shipments/{id}/pack | packShipment |
| POST | /logistics/shipments/{id}/dispatch | dispatchShipment |
| POST | /logistics/shipments/{id}/in-transit | registerShipmentInTransit |
| POST | /logistics/shipments/{id}/deliver | confirmDelivery |
| POST | /logistics/returns/{id}/receive | receiveReturn |
| GET | /logistics/orders, /shipments, /returns, /orders/{orderId}/shipment | consultas |
| PUT | /logistics/password | changePassword |

## ADMINISTRATOR (`/admin/**`)
| Método | Ruta | Operación |
|---|---|---|
| POST | /admin/sellers | registerSeller |
| POST | /admin/users | registerInternalUser |
| PUT | /admin/users/{id}/status | changeUserStatus |
| PUT | /admin/buyers/{id}/commercial-status | changeBuyerCommercialStatus |
| POST | /admin/warehouses | createWarehouse |
| PUT | /admin/warehouses/{id}/status | changeWarehouseStatus |
| POST | /admin/returns/{id}/approve | approveReturn |
| POST | /admin/returns/{id}/reject | rejectReturn |
| POST | /admin/refunds/{id}/process | processRefund |
| GET | /admin/users, /warehouses, /orders, /invoices, /shipments, /returns, /refunds | consultas globales |
| GET | /admin/inventory/product/{id}, /inventory/movements | inventario |
| GET | /admin/orders/{orderId}/invoice, /orders/{orderId}/shipment | detalle por pedido |
| GET | /admin/reports/sales | generateSalesReport |
| PUT | /admin/password | changePassword |

## SUPERVISOR (`/supervisor/**`, solo lectura)
Mismas consultas y reporte que el administrador (`GET`), más `PUT /supervisor/password`.

## Decisiones
- Las respuestas son DTOs (`records`); nunca se serializa un objeto del dominio ni el `passwordHash`.
- El usuario autenticado llega al controlador con `@AuthenticationPrincipal(expression = "user")`.
- El filtro JWT recarga el usuario desde la base de datos en cada petición, así un usuario bloqueado pierde acceso de inmediato.