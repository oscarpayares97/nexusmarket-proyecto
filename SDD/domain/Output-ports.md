# Output Ports

Package `application.domain.ports.out`. Interfaces that the domain uses to reach persistence and security without knowing the technology. Each port lists the adapter that implements it. Generated from the source code.

---

## BuyerRepositoryPort

Persistence port of the `Buyer` aggregate.

**Implemented by:** `BuyerJpaAdapter`

| Method |
|---|
| `Buyer save(Buyer buyer)` |
| `Optional<Buyer> findByUserId(Long userId)` |

---

## InventoryRepositoryPort

Persistence port of the `Inventory` aggregate.

**Implemented by:** `InventoryJpaAdapter`

| Method |
|---|
| `Inventory save(Inventory item)` |
| `Optional<Inventory> findByProductAndWarehouse(Long productId, Long warehouseId)` |
| `List<Inventory> findByProductId(Long productId)` |
| `InventoryMovement saveMovement(InventoryMovement movement)` |
| `List<InventoryMovement> findAllMovements()` |

---

## InvoiceRepositoryPort

Persistence port of the `Invoice` aggregate.

**Implemented by:** `InvoiceJpaAdapter`

| Method |
|---|
| `Invoice save(Invoice invoice)` |
| `Optional<Invoice> findByOrderId(Long orderId)` |
| `List<Invoice> findAll()` |

---

## JwtServicePort

Issuing and parsing of signed tokens.

**Implemented by:** `JwtProvider`

| Method |
|---|
| `String generate(User user)` |
| `Long extractUserId(String token)` |

---

## OrderRepositoryPort

Persistence port of the `Order` aggregate.

**Implemented by:** `OrderJpaAdapter`

| Method |
|---|
| `Order save(Order order)` |
| `Optional<Order> findById(Long id)` |
| `Optional<Order> findOpenCartByBuyerId(Long buyerId)` |
| `List<Order> findByBuyerId(Long buyerId)` |
| `List<Order> findAll()` |

---

## PasswordServicePort

Password hashing and verification.

**Implemented by:** `BCryptPasswordServiceAdapter`

| Method |
|---|
| `String encode(String rawPassword)` |
| `boolean matches(String rawPassword, String encodedPassword)` |

---

## ProductRepositoryPort

Persistence port of the `Product` aggregate.

**Implemented by:** `ProductJpaAdapter`

| Method |
|---|
| `Product save(Product product)` |
| `Optional<Product> findById(Long id)` |
| `List<Product> findBySellerId(Long sellerId)` |
| `List<Product> findPublished()` |

---

## RefundRepositoryPort

Persistence port of the `Refund` aggregate.

**Implemented by:** `RefundJpaAdapter`

| Method |
|---|
| `Refund save(Refund refund)` |
| `Optional<Refund> findById(Long id)` |
| `List<Refund> findAll()` |

---

## ReturnRepositoryPort

Persistence port of the `Return` aggregate.

**Implemented by:** `ReturnJpaAdapter`

| Method |
|---|
| `ReturnRequest save(ReturnRequest returnRequest)` |
| `Optional<ReturnRequest> findById(Long id)` |
| `List<ReturnRequest> findByOrderId(Long orderId)` |
| `List<ReturnRequest> findByBuyerId(Long buyerId)` |
| `List<ReturnRequest> findAll()` |

---

## SellerRepositoryPort

Persistence port of the `Seller` aggregate.

**Implemented by:** `SellerJpaAdapter`

| Method |
|---|
| `Seller save(Seller seller)` |
| `Optional<Seller> findByUserId(Long userId)` |

---

## ShipmentRepositoryPort

Persistence port of the `Shipment` aggregate.

**Implemented by:** `ShipmentJpaAdapter`

| Method |
|---|
| `Shipment save(Shipment shipment)` |
| `Optional<Shipment> findById(Long id)` |
| `Optional<Shipment> findByOrderId(Long orderId)` |
| `List<Shipment> findAll()` |

---

## UserRepositoryPort

Persistence port of the `User` aggregate.

**Implemented by:** `UserJpaAdapter`

| Method |
|---|
| `User save(User user)` |
| `Optional<User> findById(Long id)` |
| `Optional<User> findByEmail(String email)` |
| `boolean existsByDocument(String document)` |
| `boolean existsByEmail(String email)` |
| `List<User> findAll()` |

---

## WarehouseRepositoryPort

Persistence port of the `Warehouse` aggregate.

**Implemented by:** `WarehouseJpaAdapter`

| Method |
|---|
| `Warehouse save(Warehouse warehouse)` |
| `Optional<Warehouse> findById(Long id)` |
| `List<Warehouse> findBySellerId(Long sellerId)` |
| `List<Warehouse> findAll()` |

---
