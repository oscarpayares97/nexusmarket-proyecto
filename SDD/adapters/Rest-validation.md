# REST Validation

Request bodies are validated with Jakarta Bean Validation (`@Valid` in the controller). A violation is translated by `GlobalExceptionHandler` into HTTP 400 with the message `field: reason`. Business rules are not validated here: they live in the domain and answer 409. Generated from the request DTOs.

---

## LoginRequestDTO

| Field | Type | Constraints |
|---|---|---|
| `email` | `String` | `@NotBlank @Size(max = 100)` |
| `password` | `String` | `@NotBlank @Size(max = 100)` |

## RegisterBuyerRequestDTO

| Field | Type | Constraints |
|---|---|---|
| `document` | `String` | `@NotBlank @Size(max = 20)` |
| `fullName` | `String` | `@NotBlank @Size(max = 120)` |
| `email` | `String` | `@NotBlank @Size(max = 100)` |
| `password` | `String` | `@NotBlank @Size(min = 8, max = 100)` |
| `primaryAddress` | `String` | `@NotBlank @Size(max = 200)` |

## AddressRequest

| Field | Type | Constraints |
|---|---|---|
| `address` | `String` | `@NotBlank` |

## AddCartItemRequest

| Field | Type | Constraints |
|---|---|---|
| `productId` | `Long` | `@NotNull` |
| `quantity` | `int` | `@Min(1)` |

## ConfirmOrderRequest

| Field | Type | Constraints |
|---|---|---|
| `deliveryAddress` | `String` | `@NotBlank` |

## PayOrderRequest

| Field | Type | Constraints |
|---|---|---|
| `paymentApproved` | `boolean` | - |

## RequestReturnRequest

| Field | Type | Constraints |
|---|---|---|
| `orderId` | `Long` | `@NotNull` |
| `quantities` | `Map<Long, Integer>` | `@NotEmpty` |
| `reason` | `String` | `@NotBlank` |

## ChangePasswordRequest

| Field | Type | Constraints |
|---|---|---|
| `currentPassword` | `String` | `@NotBlank` |
| `newPassword` | `String` | `@NotBlank` |

## RegisterProductRequest

| Field | Type | Constraints |
|---|---|---|
| `name` | `String` | `@NotBlank` |
| `type` | `ProductType` | `@NotNull` |
| `price` | `BigDecimal` | `@NotNull @DecimalMin("0.01")` |

## VariantRequest

| Field | Type | Constraints |
|---|---|---|
| `variant` | `String` | `@NotBlank` |

## InventoryEntryRequest

| Field | Type | Constraints |
|---|---|---|
| `productId` | `Long` | `@NotNull` |
| `warehouseId` | `Long` | `@NotNull` |
| `quantity` | `int` | `@Min(1)` |

## InventoryAdjustRequest

| Field | Type | Constraints |
|---|---|---|
| `productId` | `Long` | `@NotNull` |
| `warehouseId` | `Long` | `@NotNull` |
| `newQuantity` | `int` | `@Min(0)` |

## InventoryStatusRequest

| Field | Type | Constraints |
|---|---|---|
| `productId` | `Long` | `@NotNull` |
| `warehouseId` | `Long` | `@NotNull` |
| `status` | `InventoryStatus` | `@NotNull` |

## DispatchRequest

| Field | Type | Constraints |
|---|---|---|
| `carrier` | `String` | `@NotBlank` |
| `trackingNumber` | `String` | `@NotBlank` |

## RegisterSellerRequest

| Field | Type | Constraints |
|---|---|---|
| `document` | `String` | `@NotBlank` |
| `fullName` | `String` | `@NotBlank` |
| `email` | `String` | `@NotBlank` |
| `rawPassword` | `String` | `@NotBlank` |
| `tradeName` | `String` | `@NotBlank` |
| `warehouseName` | `String` | `@NotBlank` |
| `warehouseLocation` | `String` | `@NotBlank` |

## RegisterInternalUserRequest

| Field | Type | Constraints |
|---|---|---|
| `document` | `String` | `@NotBlank` |
| `fullName` | `String` | `@NotBlank` |
| `email` | `String` | `@NotBlank` |
| `rawPassword` | `String` | `@NotBlank` |
| `role` | `UserRole` | `@NotNull` |

## CreateWarehouseRequest

| Field | Type | Constraints |
|---|---|---|
| `name` | `String` | `@NotBlank` |
| `location` | `String` | `@NotBlank` |
| `sellerId` | `Long` | - |

## UserStatusRequest

| Field | Type | Constraints |
|---|---|---|
| `status` | `UserStatus` | `@NotNull` |

## CommercialStatusRequest

| Field | Type | Constraints |
|---|---|---|
| `status` | `CommercialStatus` | `@NotNull` |

## WarehouseStatusRequest

| Field | Type | Constraints |
|---|---|---|
| `active` | `boolean` | - |

## RejectReturnRequest

| Field | Type | Constraints |
|---|---|---|
| `reason` | `String` | `@NotBlank` |
