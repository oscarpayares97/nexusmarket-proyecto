# Layer - Persistence Adapters

Package `application/adapters/persistence/jpa/`. Output adapters that implement the 13 output ports with Spring Data JPA over MySQL. The domain never sees JPA.

| Piece | Count | Notes |
|---|---|---|
| Adapters | 11 | `UserJpaAdapter`, `BuyerJpaAdapter`, `SellerJpaAdapter`, `WarehouseJpaAdapter`, `ProductJpaAdapter`, `InventoryJpaAdapter`, `OrderJpaAdapter`, `InvoiceJpaAdapter`, `ShipmentJpaAdapter`, `ReturnJpaAdapter`, `RefundJpaAdapter` |
| Entities | 12 | One per aggregate root |
| Spring Data repositories | 12 | `SpringDataJpa*Repository` |
| Mappers | static | `*JpaMapper`, domain <-> entity |

## Tables

| Table | Origin |
|---|---|
| `users`, `buyers`, `sellers`, `warehouses`, `products`, `inventories`, `inventory_movements`, `orders`, `invoices`, `shipments`, `return_requests`, `refunds` | 12 entities |
| `buyer_addresses`, `product_variants` | `@ElementCollection` |
| `order_items`, `invoice_lines`, `return_items` | `@Embeddable` rows (order items are reused by invoices) |

`inventories` has a unique constraint on product and warehouse.

## Mapping decisions

- Enumerations are stored as text with `name()` and rebuilt with `valueOf`.
- `Money` is stored as `BigDecimal` with precision 19 and scale 2.
- Lists are loaded eagerly (`FetchType.EAGER`) so the domain object is complete.
- After saving, an adapter returns the **same domain object** with its generated id assigned (`assignId`).
- Schema management: `spring.jpa.hibernate.ddl-auto=update`.

## Not yet implemented

MongoDB audit adapter: waiting for the professor's decision.

Generated detail: `SDD/adapters/Persistence-adapters.md` and `SDD/domain/Output-ports.md`.