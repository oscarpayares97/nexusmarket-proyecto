# Domain Model

Package `application.domain.models`. Entities and read models of the NexusMarket domain, generated from the source code. Value objects and enumerations are documented in `Domain Value Objects.md`.

---

## AuthenticationResult

Result of a successful login: the signed token and the authenticated user.

Declared as a Java `record` (immutable).

| Attribute | Type |
|---|---|
| `token` | `String` |
| `user` | `User` |

**Validations and invariants:**

- `Guard.requireText(token, "The token")`
- `Guard.requireNonNull(user, "The user")`

---

## Buyer

Buyer profile of a user: primary and additional delivery addresses and commercial status.

| Attribute | Type |
|---|---|
| `userId` | `Long` |
| `primaryAddress` | `String` |
| `additionalAddresses` | `List<String>` |
| `commercialStatus` | `CommercialStatus` |

**Behavior (business operations):**

- `void addAddress(String address)`
- `boolean hasAddress(String address)`
- `void enable()`
- `void suspend()`
- `boolean canPurchase()`

**Validations and invariants:**

- `Guard.requireNonNull(userId, "El usuario")`
- `Guard.requireNonNull(commercialStatus, "La situación comercial")`
- `Guard.requireText(primaryAddress, "La primera direccion")`
- `Guard.requireText(address, "La direccion"))`

---

## Guard

Static validation helpers used by entities and value objects (non-blank text, non-null values).

**Behavior (business operations):**

- `static String requireText(String value, String field)`
- `static void requireNonNull(Object value, String field)`
- `static void requirePositive(int value, String field)`

**Notes (from the code):**

- Exige texto no vacío y lo devuelve sin espacios sobrantes

---

## Inventory

Stock of a product in a warehouse: available and reserved quantities and operational status.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `available` | `int` |
| `reserved` | `int` |
| `status` | `InventoryStatus` |

**Behavior (business operations):**

- `void registerEntry(int units)`
- `void reserve(int units)`
- `void registerSaleExit(int units)`
- `void adjustTo(int newAvailable)`
- `void registerCustomerReturn(int units)`
- `void markDamaged()`
- `void markAvailable()`

**Validations and invariants:**

- `Guard.requireNonNull(productId, "El producto")`
- `Guard.requireNonNull(warehouseId, "El almacen")`
- `Guard.requireNonNull(status, "El estado del inventario")`
- `Guard.requirePositive(units, "La cantidad")`
- `InvalidDataException: El stock no puede ser negativo.`
- `InvalidDataException: El artículo de inventario ya tiene un identificador.`
- `InvalidDataException: No se puede reservar el stock dañado.`
- `InsufficientStockException: Stock insuficiente. Disponible: `
- `InsufficientStockException: Existencias reservadas insuficientes. Reservado: `
- `InvalidDataException: La cantidad no puede ser negativa.`

**Notes (from the code):**

- unidades que se pueden vender
- unidades apartadas para pedidos
- La venta consume unidades que ya estaban reservadas
- Ajuste: fija lo disponible a la cantidad contada físicamente

---

## InventoryMovement

Immutable record of a change in stock (entry, reservation, sale, adjustment or return).

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `type` | `MovementType` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `quantity` | `int` |
| `userId` | `Long` |
| `occurredAt` | `LocalDateTime` |

**Validations and invariants:**

- `Guard.requireNonNull(type, "El tipo de movimiento")`
- `Guard.requireNonNull(productId, "El producto")`
- `Guard.requireNonNull(warehouseId, "El almacen")`
- `Guard.requireNonNull(userId, "El usuario")`
- `Guard.requireNonNull(occurredAt, "La fecha")`
- `InvalidDataException: La cantidad no puede ser negativa.`
- `InvalidDataException: El movimiento ya tiene un identificador.`

---

## Invoice

Immutable billing document issued once per paid order.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `buyerName` | `String` |
| `buyerDocument` | `String` |
| `lines` | `List<OrderItem>` |
| `total` | `Money` |
| `issuedAt` | `LocalDateTime` |

**Behavior (business operations):**

- `static Invoice issueFor(Order order, User buyer)`

**Validations and invariants:**

- `Guard.requireNonNull(orderId, "El pedido")`
- `Guard.requireNonNull(buyerId, "El vendedor")`
- `Guard.requireNonNull(lines, "Las lineas")`
- `Guard.requireNonNull(total, "El total")`
- `Guard.requireNonNull(issuedAt, "La fecha de emisión")`
- `Guard.requireText(buyerName, "Nombre vendedor")`
- `Guard.requireText(buyerDocument, "Documento del vendedor")`
- `Guard.requireNonNull(order, "El pedido")`
- `Guard.requireNonNull(buyer, "El vendedor")`
- `InvalidDataException: Una factura debe contener al menos una línea.`
- `InvalidStatusTransitionException: Solo se puede emitir una factura para un pedido pagado.`
- `InvalidDataException: La factura ya tiene un número de identificación.`

**Notes (from the code):**

- Emite la factura de un pedido ya pagado

---

## Order

Purchase of a buyer. While open it is the cart; afterwards it moves through pending payment, paid, shipped and delivered.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `buyerId` | `Long` |
| `items` | `List<OrderItem>` |
| `status` | `OrderStatus` |
| `deliveryAddress` | `String` |
| `createdAt` | `LocalDateTime` |

**Behavior (business operations):**

- `void addItem(Product product, int quantity)`
- `void removeItem(Long productId)`
- `Money total()`
- `boolean onlyDigital()`
- `void confirm(String deliveryAddress)`
- `void markPaid()`
- `void markShipped()`
- `void markDelivered()`

**Validations and invariants:**

- `Guard.requireNonNull(buyerId, "El vendedor")`
- `Guard.requireNonNull(status, "El estado")`
- `Guard.requireNonNull(createdAt, "La fecha de creación")`
- `Guard.requirePositive(quantity, "La cantidad")`
- `Guard.requireText(deliveryAddress, "La dirección de entrega")`
- `InvalidDataException: El pedido ya tiene un número de identificación.`
- `InvalidDataException: Ese producto no esta en la canasta.`
- `InvalidDataException: El carrito esta vacio.`
- `InvalidStatusTransitionException: Un pedido entregado no se puede modificar bajo ninguna circunstancia.`
- `InvalidStatusTransitionException: No se puede `

**Notes (from the code):**

- ---------- Carrito ----------
- Los Long se comparan con equals (no con ==)
- true si el pedido no tiene productos físicos
- ---------- Ciclo de vida ----------
- Regla central: ¿se puede hacer esta acción en el estado actual?

---

## OrderItem

Line of an order: snapshot of the product name, type, unit price, quantity and warehouse.

| Attribute | Type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `productType` | `ProductType` |
| `unitPrice` | `Money` |
| `quantity` | `int` |
| `warehouseId` | `Long` |

**Behavior (business operations):**

- `static OrderItem from(Product product, int quantity)`
- `void increase(int units)`
- `void assignWarehouse(Long warehouseId)`
- `Money subtotal()`
- `boolean isPhysical()`

**Validations and invariants:**

- `Guard.requireNonNull(productId, "El producto")`
- `Guard.requireNonNull(productType, "El tipo de producto")`
- `Guard.requireNonNull(unitPrice, "El precio de unidad")`
- `Guard.requirePositive(quantity, "La cantidad")`
- `Guard.requireText(productName, "El nombre del producto")`
- `Guard.requirePositive(units, "La cantidad")`

**Notes (from the code):**

- copia del precio al momento de la compra
- bodega donde se reservó (null si es digital)

---

## Product

Item offered in the catalog by a seller: physical or digital, with price, variants and publication status.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `sellerId` | `Long` |
| `name` | `String` |
| `type` | `ProductType` |
| `price` | `Money` |
| `variants` | `List<String>` |
| `status` | `ProductStatus` |

**Behavior (business operations):**

- `void addVariant(String variant)`
- `void publish()`
- `void suspend()`
- `void discontinue()`
- `boolean isPublished()`
- `boolean isPhysical()`

**Validations and invariants:**

- `Guard.requireNonNull(sellerId, "Vendedor")`
- `Guard.requireNonNull(type, "Tipo de producto")`
- `Guard.requireNonNull(price, "Precio")`
- `Guard.requireNonNull(status, "Estado")`
- `Guard.requireText(name, "El nombre el producto")`
- `Guard.requireText(variant, "La variante")`
- `InvalidDataException: El precio debe ser mayor que cero.`
- `InvalidDataException: El producto ya tiene un identificador.`
- `InvalidDataException: Esta variante ya está disponible en este producto.`
- `InvalidStatusTransitionException: Un producto descatalogado no puede ser `

**Notes (from the code):**

- Un producto nuevo nace SUSPENDED (no visible) hasta que el vendedor lo publica

---

## Refund

Money reimbursed to the buyer for a received return.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `returnRequestId` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `amount` | `Money` |
| `status` | `RefundStatus` |
| `createdAt` | `LocalDateTime` |
| `processedAt` | `LocalDateTime` |

**Behavior (business operations):**

- `static Refund forReturn(ReturnRequest returnRequest)`
- `void process()`

**Validations and invariants:**

- `Guard.requireNonNull(returnRequestId, "El regreso")`
- `Guard.requireNonNull(orderId, "La orden")`
- `Guard.requireNonNull(buyerId, "El vendedor")`
- `Guard.requireNonNull(amount, "La cantidad")`
- `Guard.requireNonNull(status, "El estado")`
- `Guard.requireNonNull(createdAt, "Fecha de creacion")`
- `InvalidDataException: El importe del reembolso debe ser superior a cero.`
- `InvalidStatusTransitionException: Solo se puede crear un reembolso por una devolución recibida.`
- `InvalidDataException: El reembolso ya tiene un número de referencia.`
- `InvalidStatusTransitionException: El reembolso ya se ha tramitado.`

**Notes (from the code):**

- El reembolso solo se crea para una devolución ya recibida en bodega

---

## ReturnItem

Line of a return request: product, quantity and unit price snapshot.

| Attribute | Type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `quantity` | `int` |
| `unitPrice` | `Money` |
| `warehouseId` | `Long` |

**Behavior (business operations):**

- `static ReturnItem from(OrderItem item, int quantity)`
- `Money subtotal()`

**Validations and invariants:**

- `Guard.requireNonNull(productId, "El producto")`
- `Guard.requirePositive(quantity, "La cantidad")`
- `Guard.requireNonNull(unitPrice, "Precio de la unidad")`
- `Guard.requireNonNull(warehouseId, "El almacen")`
- `Guard.requireText(productName, "El nombre del producto")`
- `InvalidDataException: Los productos digitales no se pueden devolver.`
- `InvalidDataException: No se pueden devolver más unidades de las que se compraron.`

---

## ReturnRequest

Request of a buyer to return items of a delivered order.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `items` | `List<ReturnItem>` |
| `reason` | `String` |
| `status` | `ReturnStatus` |
| `rejectionReason` | `String` |
| `requestedAt` | `LocalDateTime` |

**Behavior (business operations):**

- `Money total()`
- `void approve()`
- `void reject(String rejectionReason)`
- `void markReceived()`

**Validations and invariants:**

- `Guard.requireNonNull(orderId, "El pedido")`
- `Guard.requireNonNull(buyerId, "El vendedor")`
- `Guard.requireNonNull(status, "El estado")`
- `Guard.requireNonNull(requestedAt, "Fecha de solicitud")`
- `Guard.requireText(reason, "La razon")`
- `Guard.requireText(rejectionReason, "The rejection reason")`
- `InvalidDataException: Una devolución debe incluir al menos un producto.`
- `InvalidDataException: La devolución ya tiene un número de referencia.`
- `InvalidStatusTransitionException: No se puede `

---

## SalesReport

Read model with gross sales, refunds, net sales, orders by status and sales by seller.

Declared as a Java `record` (immutable).

| Attribute | Type |
|---|---|
| `grossSales` | `Money` |
| `refunded` | `Money` |
| `netSales` | `Money` |
| `ordersByStatus` | `Map<OrderStatus, Integer>` |
| `salesBySeller` | `Map<Long, Money>` |

---

## Seller

Seller profile of a user, with its trade name.

| Attribute | Type |
|---|---|
| `userId` | `Long` |
| `tradeName` | `String` |

**Validations and invariants:**

- `Guard.requireNonNull(userId, "El usuario")`
- `Guard.requireText(tradeName, "El nombre comercial")`

---

## Shipment

Delivery of a paid order with its carrier, tracking number and lifecycle.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `deliveryAddress` | `String` |
| `status` | `ShipmentStatus` |
| `carrier` | `String` |
| `trackingNumber` | `String` |
| `createdAt` | `LocalDateTime` |
| `shippedAt` | `LocalDateTime` |
| `deliveredAt` | `LocalDateTime` |

**Behavior (business operations):**

- `void pack()`
- `void dispatch(String carrier, String trackingNumber)`
- `void markInTransit()`
- `void confirmDelivery()`

**Validations and invariants:**

- `Guard.requireNonNull(orderId, "La orden")`
- `Guard.requireNonNull(status, "El estado")`
- `Guard.requireNonNull(createdAt, "Fecha de creacion")`
- `Guard.requireText(deliveryAddress, "La dirección de entrega")`
- `Guard.requireText(carrier, "Compañia aerea")`
- `Guard.requireText(trackingNumber, "El número de seguimiento")`
- `InvalidDataException: El envío ya tiene un número de identificación.`
- `InvalidStatusTransitionException: No se puede `

---

## User

System identity with a single role (RG-02) and an access status. Identified by its document number and e-mail.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `document` | `DocumentNumber` |
| `fullName` | `String` |
| `email` | `Email` |
| `passwordHash` | `String` |
| `role` | `UserRole` |
| `status` | `UserStatus` |

**Behavior (business operations):**

- `void changePasswordHash(String newPasswordHash)`
- `void block()`
- `void deactivate()`
- `void activate()`
- `boolean isActive()`

**Validations and invariants:**

- `Guard.requireText(fullName, "El nombre completo")`
- `Guard.requireText(passwordHash, "La contraseña")`
- `Guard.requireNonNull(role, "ROL")`
- `Guard.requireNonNull(status, "El estado")`
- `Guard.requireText(newPasswordHash, "La contraseña")`
- `InvalidDataException: El usuario ya tiene un ID.`

**Notes (from the code):**

- un único rol por usuario (RG-02)
- Para crear un usuario nuevo (inicia ACTIVE)
- Para reconstruir un usuario guardado (conserva su estado)

---

## Warehouse

Place where stock is kept; it belongs to the marketplace or to a seller.

| Attribute | Type |
|---|---|
| `id` | `Long` |
| `name` | `String` |
| `location` | `String` |
| `type` | `WarehouseType` |
| `sellerId` | `Long` |
| `active` | `boolean` |

**Behavior (business operations):**

- `void activate()`
- `void deactivate()`
- `boolean isActive()`

**Validations and invariants:**

- `Guard.requireText(name, "Nombre del almacen")`
- `Guard.requireText(location, "Ubicacion del almacen")`
- `Guard.requireNonNull(type, "Tipo de almacen")`
- `InvalidDataException: Un almacén de vendedor debe tener un vendedor asociado.`
- `InvalidDataException: Un almacén de una plataforma de comercio electrónico no pertenece a un vendedor.`
- `InvalidDataException: El almacén ya tiene un identificador.`

**Notes (from the code):**

- null si es una bodega del Marketplace

---
