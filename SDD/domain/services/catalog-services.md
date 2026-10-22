# Catalog Management Services

Package `application.domain.services.catalog`. 7 classes. Generated from the source code.

## Add Product Variant

Adds a variant (for example size or color) to a product of the authenticated seller.

- **Class:** `application.domain.services.catalog.AddProductVariantService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Product add(User actor, Long productId, String variant)`

**Collaborators (injected through the constructor):**

- `OwnedProductLoader`
- `ProductRepositoryPort`

**Covered by tests:** `CatalogManagementServicesTest`

---

## Consult Published Products

Lists the published products of the catalog. Available without authentication.

- **Class:** `application.domain.services.catalog.ConsultPublishedProductsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `List<Product> consult()`

**Collaborators (injected through the constructor):**

- `ProductRepositoryPort`

**Covered by tests:** `UseCaseImplDelegationTest`, `CatalogServicesTest`

---

## Discontinue Product

Permanently retires a product from sale.

- **Class:** `application.domain.services.catalog.DiscontinueProductService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Product discontinue(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `OwnedProductLoader`
- `ProductRepositoryPort`

**Covered by tests:** `CatalogManagementServicesTest`

---

## Owned Product Loader

Internal helper shared by the catalog services: checks that the actor is a seller, loads the product and verifies that the seller owns it.

- **Class:** `application.domain.services.catalog.OwnedProductLoader`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `SELLER`
- **Operation:** `Product load(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. |

**Covered by tests:** `CatalogManagementServicesTest`

---

## Publish Product

Publishes a product so that it appears in the public catalog.

- **Class:** `application.domain.services.catalog.PublishProductService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `SELLER`
- **Operation:** `Product publish(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. |

**Covered by tests:** `CatalogServicesTest`

---

## Register Product

Creates a product owned by the authenticated seller.

- **Class:** `application.domain.services.catalog.RegisterProductService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `SELLER`
- **Operation:** `Product register(User actor, RegisterProductCommand command)`

**Collaborators (injected through the constructor):**

- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `CatalogServicesTest`

---

## Suspend Product

Temporarily removes a product from the public catalog.

- **Class:** `application.domain.services.catalog.SuspendProductService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Product suspend(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `OwnedProductLoader`
- `ProductRepositoryPort`

**Covered by tests:** `UseCaseImplDelegationTest`, `CatalogManagementServicesTest`

---
