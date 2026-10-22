# Persistence Adapters

Package `application.adapters.persistence.jpa`. Each adapter implements an output port of the domain with Spring Data JPA over MySQL. The domain never sees JPA: entities are converted with static mappers (`*JpaMapper`). Enumerations are stored as text (`name()`) and money as `DECIMAL(19,2)`. Generated from the source code.

---

## Adapters

| Adapter | Implements (output port) | Spring Data repositories | Mappers |
|---|---|---|---|
| `BuyerJpaAdapter` | `BuyerRepositoryPort` | `SpringDataJpaBuyerRepository` | `BuyerJpaMapper` |
| `InventoryJpaAdapter` | `InventoryRepositoryPort` | `SpringDataJpaInventoryMovementRepository`, `SpringDataJpaInventoryRepository` | `InventoryJpaMapper` |
| `InvoiceJpaAdapter` | `InvoiceRepositoryPort` | `SpringDataJpaInvoiceRepository` | `InvoiceJpaMapper` |
| `OrderJpaAdapter` | `OrderRepositoryPort` | `SpringDataJpaOrderRepository` | `OrderJpaMapper` |
| `ProductJpaAdapter` | `ProductRepositoryPort` | `SpringDataJpaProductRepository` | `ProductJpaMapper` |
| `RefundJpaAdapter` | `RefundRepositoryPort` | `SpringDataJpaRefundRepository` | `ReturnJpaMapper` |
| `ReturnJpaAdapter` | `ReturnRepositoryPort` | `SpringDataJpaReturnRepository` | `ReturnJpaMapper` |
| `SellerJpaAdapter` | `SellerRepositoryPort` | `SpringDataJpaSellerRepository` | `SellerJpaMapper` |
| `ShipmentJpaAdapter` | `ShipmentRepositoryPort` | `SpringDataJpaShipmentRepository` | `ShipmentJpaMapper` |
| `UserJpaAdapter` | `UserRepositoryPort` | `SpringDataJpaUserRepository` | `UserJpaMapper` |
| `WarehouseJpaAdapter` | `WarehouseRepositoryPort` | `SpringDataJpaWarehouseRepository` | `WarehouseJpaMapper` |

## Tables (JPA entities)

## Spring Data repositories

| Repository | Entity |
|---|---|
| `SpringDataJpaBuyerRepository` | `BuyerJpaEntity` |
| `SpringDataJpaInventoryMovementRepository` | `InventoryMovementJpaEntity` |
| `SpringDataJpaInventoryRepository` | `InventoryJpaEntity` |
| `SpringDataJpaInvoiceRepository` | `InvoiceJpaEntity` |
| `SpringDataJpaOrderRepository` | `OrderJpaEntity` |
| `SpringDataJpaProductRepository` | `ProductJpaEntity` |
| `SpringDataJpaRefundRepository` | `RefundJpaEntity` |
| `SpringDataJpaReturnRepository` | `ReturnRequestJpaEntity` |
| `SpringDataJpaSellerRepository` | `SellerJpaEntity` |
| `SpringDataJpaShipmentRepository` | `ShipmentJpaEntity` |
| `SpringDataJpaUserRepository` | `UserJpaEntity` |
| `SpringDataJpaWarehouseRepository` | `WarehouseJpaEntity` |
