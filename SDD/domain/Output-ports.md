# Output Ports: NexusMarket

Paquete `application.domain.ports.out`. Interfaces que el dominio exige a la infraestructura.
Las implementan los adaptadores de persistencia (JPA/MySQL) y de seguridad.

| Puerto | Operaciones | Modelo |
|---|---|---|
| UserRepositoryPort | save, findById, findByEmail, existsByDocument, existsByEmail, findAll | User |
| BuyerRepositoryPort | save, findByUserId | Buyer |
| SellerRepositoryPort | save, findByUserId | Seller |
| WarehouseRepositoryPort | save, findById, findBySellerId, findAll | Warehouse |
| ProductRepositoryPort | save, findById, findBySellerId, findPublished | Product |
| InventoryRepositoryPort | save, findByProductAndWarehouse, findByProductId, saveMovement, findAllMovements | Inventory, InventoryMovement |
| OrderRepositoryPort | save, findById, findOpenCartByBuyerId, findByBuyerId, findAll | Order |
| InvoiceRepositoryPort | save, findByOrderId, findAll | Invoice |
| ShipmentRepositoryPort | save, findById, findByOrderId, findAll | Shipment |
| ReturnRepositoryPort | save, findById, findByOrderId, findAll | ReturnRequest |
| RefundRepositoryPort | save, findById, findAll | Refund |
| PasswordServicePort | encode, matches | Servicio técnico (BCrypt) |

## Pendiente
JwtServicePort (token de sesión), AuthorizationPort y AuditLogRepositoryPort (si la bitácora aplica a NexusMarket).

# Output Ports: NexusMarket

Paquete `application.domain.ports.out`. Interfaces que el dominio exige a la infraestructura.
Las implementan los adaptadores de persistencia (JPA/MySQL) y de seguridad.

| Puerto | Operaciones | Modelo |
|---|---|---|
| UserRepositoryPort | save, findById, findByEmail, existsByDocument, existsByEmail, findAll | User |
| BuyerRepositoryPort | save, findByUserId | Buyer |
| SellerRepositoryPort | save, findByUserId | Seller |
| WarehouseRepositoryPort | save, findById, findBySellerId, findAll | Warehouse |
| ProductRepositoryPort | save, findById, findBySellerId, findPublished | Product |
| InventoryRepositoryPort | save, findByProductAndWarehouse, findByProductId, saveMovement, findAllMovements | Inventory, InventoryMovement |
| OrderRepositoryPort | save, findById, findOpenCartByBuyerId, findByBuyerId, findAll | Order |
| InvoiceRepositoryPort | save, findByOrderId, findAll | Invoice |
| ShipmentRepositoryPort | save, findById, findByOrderId, findAll | Shipment |
| ReturnRepositoryPort | save, findById, findByOrderId, findAll | ReturnRequest |
| RefundRepositoryPort | save, findById, findAll | Refund |
| PasswordServicePort | encode, matches | Servicio técnico (BCrypt) |
| JwtServicePort | generate(User), extractUserId(token) | Servicio técnico (JWT) |

## Comparación con el proyecto de referencia
| Puerto de referencia | Estado en NexusMarket |
|---|---|
| CustomerRepositoryPort | Equivalente: BuyerRepositoryPort y SellerRepositoryPort |
| Repositorios de cuenta, préstamo, transferencia y operación | Equivalentes: un repositorio por agregado del negocio |
| AuthorizationPort | Pendiente de analizar |
| BusinessConfigurationPort | No aplica: la especificación no define parámetros configurables |
| NotificationPort | No aplica: la especificación no pide notificaciones |
| AuditLogRepositoryPort | Pendiente: depende de si la bitácora NoSQL aplica a NexusMarket |