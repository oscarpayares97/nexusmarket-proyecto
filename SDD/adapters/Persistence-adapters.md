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

### BuyerJpaEntity

- **Kind:** Entity
- **Table:** `buyers`

| Column / attribute | Java type |
|---|---|
| `userId` | `Long` |
| `primaryAddress` | `String` |
| `commercialStatus` | `String` |
| `additionalAddresses` | `List<String>` |

### InventoryJpaEntity

- **Kind:** Entity
- **Table:** `inventories`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `available` | `int` |
| `reserved` | `int` |
| `status` | `String` |

### InventoryMovementJpaEntity

- **Kind:** Entity
- **Table:** `inventory_movements`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `type` | `String` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `quantity` | `int` |
| `userId` | `Long` |
| `occurredAt` | `LocalDateTime` |

### InvoiceJpaEntity

- **Kind:** Entity
- **Table:** `invoices`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `buyerName` | `String` |
| `buyerDocument` | `String` |
| `total` | `BigDecimal` |
| `issuedAt` | `LocalDateTime` |
| `lines` | `List<OrderItemJpaEmbeddable>` |

### OrderItemJpaEmbeddable

- **Kind:** Embeddable
- **Table:** `(embedded in its parent table)`

| Column / attribute | Java type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `productType` | `String` |
| `unitPrice` | `BigDecimal` |
| `quantity` | `int` |
| `warehouseId` | `Long` |

### OrderJpaEntity

- **Kind:** Entity
- **Table:** `orders`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `buyerId` | `Long` |
| `status` | `String` |
| `deliveryAddress` | `String` |
| `createdAt` | `LocalDateTime` |
| `items` | `List<OrderItemJpaEmbeddable>` |

### ProductJpaEntity

- **Kind:** Entity
- **Table:** `products`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `sellerId` | `Long` |
| `name` | `String` |
| `type` | `String` |
| `price` | `BigDecimal` |
| `status` | `String` |
| `variants` | `List<String>` |

### RefundJpaEntity

- **Kind:** Entity
- **Table:** `refunds`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `returnRequestId` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `amount` | `BigDecimal` |
| `status` | `String` |
| `createdAt` | `LocalDateTime` |
| `processedAt` | `LocalDateTime` |

### ReturnItemJpaEmbeddable

- **Kind:** Embeddable
- **Table:** `(embedded in its parent table)`

| Column / attribute | Java type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `quantity` | `int` |
| `unitPrice` | `BigDecimal` |
| `warehouseId` | `Long` |

### ReturnRequestJpaEntity

- **Kind:** Entity
- **Table:** `return_requests`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `reason` | `String` |
| `status` | `String` |
| `rejectionReason` | `String` |
| `requestedAt` | `LocalDateTime` |
| `items` | `List<ReturnItemJpaEmbeddable>` |

### SellerJpaEntity

- **Kind:** Entity
- **Table:** `sellers`

| Column / attribute | Java type |
|---|---|
| `userId` | `Long` |
| `tradeName` | `String` |

### ShipmentJpaEntity

- **Kind:** Entity
- **Table:** `shipments`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `deliveryAddress` | `String` |
| `status` | `String` |
| `carrier` | `String` |
| `trackingNumber` | `String` |
| `createdAt` | `LocalDateTime` |
| `shippedAt` | `LocalDateTime` |
| `deliveredAt` | `LocalDateTime` |

### UserJpaEntity

- **Kind:** Entity
- **Table:** `users`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `document` | `String` |
| `fullName` | `String` |
| `email` | `String` |
| `passwordHash` | `String` |
| `role` | `String` |
| `status` | `String` |

### WarehouseJpaEntity

- **Kind:** Entity
- **Table:** `warehouses`

| Column / attribute | Java type |
|---|---|
| `id` | `Long` |
| `name` | `String` |
| `location` | `String` |
| `type` | `String` |
| `sellerId` | `Long` |
| `active` | `boolean` |

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
