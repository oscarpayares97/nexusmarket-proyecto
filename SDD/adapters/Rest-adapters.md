# REST Adapters

Package `application.adapters.rest`. Translates HTTP into calls to the input ports and domain objects into JSON. Controllers do not contain business rules. Generated from the source code.

---

## Controllers

See `Api-rest-endpoints.md` for every route.

- `AdminRestController           22 endpoints`
- `AuthRestController             2 endpoints`
- `BuyerRestController           11 endpoints`
- `LogisticsRestController       17 endpoints`
- `PublicRestController           1 endpoints`
- `SellerRestController          11 endpoints`
- `SupervisorRestController      13 endpoints`

## Requests DTOs

### LoginRequestDTO

| Field | Type |
|---|---|
| `email` | `String` |
| `password` | `String` |

### RegisterBuyerRequestDTO

| Field | Type |
|---|---|
| `document` | `String` |
| `fullName` | `String` |
| `email` | `String` |
| `password` | `String` |
| `primaryAddress` | `String` |

### AddressRequest

| Field | Type |
|---|---|
| `address` | `String` |

### AddCartItemRequest

| Field | Type |
|---|---|
| `productId` | `Long` |
| `quantity` | `int` |

### ConfirmOrderRequest

| Field | Type |
|---|---|
| `deliveryAddress` | `String` |

### PayOrderRequest

| Field | Type |
|---|---|
| `paymentApproved` | `boolean` |

### RequestReturnRequest

| Field | Type |
|---|---|
| `orderId` | `Long` |
| `quantities` | `Map<Long, Integer>` |
| `reason` | `String` |

### ChangePasswordRequest

| Field | Type |
|---|---|
| `currentPassword` | `String` |
| `newPassword` | `String` |

### RegisterProductRequest

| Field | Type |
|---|---|
| `name` | `String` |
| `type` | `ProductType` |
| `price` | `BigDecimal` |

### VariantRequest

| Field | Type |
|---|---|
| `variant` | `String` |

### InventoryEntryRequest

| Field | Type |
|---|---|
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `quantity` | `int` |

### InventoryAdjustRequest

| Field | Type |
|---|---|
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `newQuantity` | `int` |

### InventoryStatusRequest

| Field | Type |
|---|---|
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `status` | `InventoryStatus` |

### DispatchRequest

| Field | Type |
|---|---|
| `carrier` | `String` |
| `trackingNumber` | `String` |

### RegisterSellerRequest

| Field | Type |
|---|---|
| `document` | `String` |
| `fullName` | `String` |
| `email` | `String` |
| `rawPassword` | `String` |
| `tradeName` | `String` |
| `warehouseName` | `String` |
| `warehouseLocation` | `String` |

### RegisterInternalUserRequest

| Field | Type |
|---|---|
| `document` | `String` |
| `fullName` | `String` |
| `email` | `String` |
| `rawPassword` | `String` |
| `role` | `UserRole` |

### CreateWarehouseRequest

| Field | Type |
|---|---|
| `name` | `String` |
| `location` | `String` |
| `sellerId` | `Long` |

### UserStatusRequest

| Field | Type |
|---|---|
| `status` | `UserStatus` |

### CommercialStatusRequest

| Field | Type |
|---|---|
| `status` | `CommercialStatus` |

### WarehouseStatusRequest

| Field | Type |
|---|---|
| `active` | `boolean` |

### RejectReturnRequest

| Field | Type |
|---|---|
| `reason` | `String` |

## Responses DTOs

### BuyerResponseDTO

| Field | Type |
|---|---|
| `userId` | `Long` |
| `primaryAddress` | `String` |
| `additionalAddresses` | `List<String>` |
| `commercialStatus` | `String` |

### LoginResponseDTO

| Field | Type |
|---|---|
| `token` | `String` |
| `tokenType` | `String` |
| `expiresIn` | `Long` |
| `user` | `UserInfo` |
| `userId` | `Long` |
| `fullName` | `String` |
| `email` | `String` |
| `role` | `String` |

### UserResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `document` | `String` |
| `fullName` | `String` |
| `email` | `String` |
| `role` | `String` |
| `status` | `String` |

### BuyerProfileResponse

| Field | Type |
|---|---|
| `userId` | `Long` |
| `primaryAddress` | `String` |
| `additionalAddresses` | `List<String>` |
| `commercialStatus` | `String` |

### SellerResponse

| Field | Type |
|---|---|
| `userId` | `Long` |
| `tradeName` | `String` |

### WarehouseResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `name` | `String` |
| `location` | `String` |
| `type` | `String` |
| `sellerId` | `Long` |

### ProductResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `sellerId` | `Long` |
| `name` | `String` |
| `type` | `String` |
| `price` | `BigDecimal` |
| `status` | `String` |
| `variants` | `List<String>` |

### OrderItemResponse

| Field | Type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `productType` | `String` |
| `unitPrice` | `BigDecimal` |
| `quantity` | `int` |
| `subtotal` | `BigDecimal` |
| `warehouseId` | `Long` |

### OrderResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `buyerId` | `Long` |
| `status` | `String` |
| `deliveryAddress` | `String` |
| `createdAt` | `LocalDateTime` |
| `items` | `List<OrderItemResponse>` |
| `total` | `BigDecimal` |

### InvoiceResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `number` | `String` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `buyerName` | `String` |
| `buyerDocument` | `String` |
| `lines` | `List<OrderItemResponse>` |
| `total` | `BigDecimal` |
| `issuedAt` | `LocalDateTime` |

### ShipmentResponse

| Field | Type |
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

### ReturnItemResponse

| Field | Type |
|---|---|
| `productId` | `Long` |
| `productName` | `String` |
| `quantity` | `int` |
| `unitPrice` | `BigDecimal` |
| `subtotal` | `BigDecimal` |

### ReturnResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `items` | `List<ReturnItemResponse>` |
| `reason` | `String` |
| `status` | `String` |
| `rejectionReason` | `String` |
| `requestedAt` | `LocalDateTime` |
| `total` | `BigDecimal` |

### RefundResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `returnRequestId` | `Long` |
| `orderId` | `Long` |
| `buyerId` | `Long` |
| `amount` | `BigDecimal` |
| `status` | `String` |
| `createdAt` | `LocalDateTime` |
| `processedAt` | `LocalDateTime` |

### InventoryResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `available` | `int` |
| `reserved` | `int` |
| `status` | `String` |

### MovementResponse

| Field | Type |
|---|---|
| `id` | `Long` |
| `type` | `String` |
| `productId` | `Long` |
| `warehouseId` | `Long` |
| `quantity` | `int` |
| `userId` | `Long` |
| `occurredAt` | `LocalDateTime` |

### SalesReportResponse

| Field | Type |
|---|---|
| `grossSales` | `BigDecimal` |
| `refunded` | `BigDecimal` |
| `netSales` | `BigDecimal` |
| `ordersByStatus` | `Map<String, Integer>` |
| `salesBySeller` | `Map<Long, BigDecimal>` |

## Mappers

### BuyerRestMapper

- `BuyerResponseDTO toResponseDTO(Buyer buyer)`

### DomainRestMapper

- `<T, R> List<R> mapList(List<T> source, Function<T, R> mapper)`
- `UserResponse toUser(User user)`
- `BuyerProfileResponse toBuyer(Buyer buyer)`
- `SellerResponse toSeller(Seller seller)`
- `WarehouseResponse toWarehouse(Warehouse warehouse)`
- `ProductResponse toProduct(Product product)`
- `OrderItemResponse toOrderItem(OrderItem item)`
- `OrderResponse toOrder(Order order)`
- `InvoiceResponse toInvoice(Invoice invoice)`
- `ShipmentResponse toShipment(Shipment shipment)`
- `ReturnItemResponse toReturnItem(ReturnItem item)`
- `ReturnResponse toReturn(ReturnRequest request)`
- `RefundResponse toRefund(Refund refund)`
- `InventoryResponse toInventory(Inventory inventory)`
- `MovementResponse toMovement(InventoryMovement movement)`
- `SalesReportResponse toSalesReport(SalesReport report)`

### UserRestMapper

- `LoginResponseDTO toLoginResponse(AuthenticationResult result, long expiresInSeconds)`
