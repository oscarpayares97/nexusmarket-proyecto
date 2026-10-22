# REST API Endpoints

Base path `/api/v1`. JSON bodies. Authentication with a JWT in the header `Authorization: Bearer <token>` (obtained with `POST /api/v1/auth/login`). The path prefix defines the role required by `SecurityConfig`; in addition, every domain service validates the role of the actor. Generated from the controllers.

Controllers only depend on the input ports (`application.domain.ports.in`) and convert with DTOs and mappers; they never call domain services directly.

---

## AdminRestController

Base `/api/v1/admin` - access: **ADMINISTRATOR**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| POST | `/api/v1/admin/sellers` | `RegisterSellerCommand` | `RegisterSellerRequest` | 201 | `registerSeller` |
| POST | `/api/v1/admin/users` | `RegisterInternalUserCommand` | `RegisterInternalUserRequest` | 201 | `registerInternalUser` |
| PUT | `/api/v1/admin/users/{userId}/status` | `changeUserStatus` | `UserStatusRequest` | 200 | `changeUserStatus` |
| PUT | `/api/v1/admin/buyers/{buyerUserId}/commercial-status` | `changeBuyerCommercialStatus` | `CommercialStatusRequest` | 200 | `changeBuyerCommercialStatus` |
| POST | `/api/v1/admin/warehouses` | `CreateWarehouseCommand` | `CreateWarehouseRequest` | 201 | `createWarehouse` |
| PUT | `/api/v1/admin/warehouses/{warehouseId}/status` | `changeWarehouseStatus` | `WarehouseStatusRequest` | 200 | `changeWarehouseStatus` |
| POST | `/api/v1/admin/returns/{returnId}/approve` | `approveReturn` | - | 200 | `approveReturn` |
| POST | `/api/v1/admin/returns/{returnId}/reject` | `rejectReturn` | `RejectReturnRequest` | 200 | `rejectReturn` |
| POST | `/api/v1/admin/refunds/{refundId}/process` | `processRefund` | - | 200 | `processRefund` |
| GET | `/api/v1/admin/users` | `consultUsers` | - | 200 | `consultUsers` |
| GET | `/api/v1/admin/warehouses` | `consultWarehouses` | - | 200 | `consultWarehouses` |
| GET | `/api/v1/admin/inventory/product/{productId}` | `consultInventory` | - | 200 | `consultInventory` |
| GET | `/api/v1/admin/inventory/movements` | `consultInventoryMovements` | - | 200 | `consultInventoryMovements` |
| GET | `/api/v1/admin/orders` | `consultOrders` | - | 200 | `consultOrders` |
| GET | `/api/v1/admin/invoices` | `consultInvoices` | - | 200 | `consultInvoices` |
| GET | `/api/v1/admin/orders/{orderId}/invoice` | `consultInvoice` | - | 200 | `consultInvoice` |
| GET | `/api/v1/admin/shipments` | `consultShipments` | - | 200 | `consultShipments` |
| GET | `/api/v1/admin/orders/{orderId}/shipment` | `consultShipment` | - | 200 | `consultShipment` |
| GET | `/api/v1/admin/returns` | `consultReturns` | - | 200 | `consultReturns` |
| GET | `/api/v1/admin/refunds` | `consultRefunds` | - | 200 | `consultRefunds` |
| GET | `/api/v1/admin/reports/sales` | `generateSalesReport` | - | 200 | `generateSalesReport` |
| PUT | `/api/v1/admin/password` | `changePassword` | `ChangePasswordRequest` | 204 | `changePassword` |

## AuthRestController

Base `/api/v1/auth` - access: **Public**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| POST | `/api/v1/auth/login` | `login` | `LoginRequestDTO` | 200 | `login` |
| POST | `/api/v1/auth/register/buyer` | `registerBuyer` | `RegisterBuyerRequestDTO` | 200 | `registerBuyer` |

## BuyerRestController

Base `/api/v1/buyer` - access: **BUYER**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| POST | `/api/v1/buyer/addresses` | `addAddress` | `AddressRequest` | 201 | `addAddress` |
| POST | `/api/v1/buyer/cart/items` | `addItemToCart` | `AddCartItemRequest` | 201 | `addItemToCart` |
| DELETE | `/api/v1/buyer/cart/items/{productId}` | `removeItemFromCart` | - | 200 | `removeItemFromCart` |
| POST | `/api/v1/buyer/orders/confirm` | `confirmOrder` | `ConfirmOrderRequest` | 200 | `confirmOrder` |
| POST | `/api/v1/buyer/orders/{orderId}/pay` | `payOrder` | `PayOrderRequest` | 200 | `payOrder` |
| GET | `/api/v1/buyer/orders` | `consultMyOrders` | - | 200 | `consultMyOrders` |
| GET | `/api/v1/buyer/orders/{orderId}/invoice` | `consultInvoice` | - | 200 | `consultInvoice` |
| GET | `/api/v1/buyer/orders/{orderId}/shipment` | `consultShipment` | - | 200 | `consultShipment` |
| POST | `/api/v1/buyer/returns` | `RequestReturnCommand` | `RequestReturnRequest` | 201 | `requestReturn` |
| GET | `/api/v1/buyer/returns` | `consultMyReturns` | - | 200 | `consultMyReturns` |
| PUT | `/api/v1/buyer/password` | `changePassword` | `ChangePasswordRequest` | 204 | `changePassword` |

## LogisticsRestController

Base `/api/v1/logistics` - access: **LOGISTICS_OPERATOR**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| POST | `/api/v1/logistics/inventory/entries` | `registerInventoryEntry` | `InventoryEntryRequest` | 201 | `registerInventoryEntry` |
| PUT | `/api/v1/logistics/inventory/adjustment` | `adjustInventory` | `InventoryAdjustRequest` | 200 | `adjustInventory` |
| PUT | `/api/v1/logistics/inventory/status` | `changeInventoryStatus` | `InventoryStatusRequest` | 200 | `changeInventoryStatus` |
| GET | `/api/v1/logistics/inventory/product/{productId}` | `consultInventory` | - | 200 | `consultInventory` |
| GET | `/api/v1/logistics/inventory/movements` | `consultInventoryMovements` | - | 200 | `consultInventoryMovements` |
| GET | `/api/v1/logistics/warehouses` | `consultWarehouses` | - | 200 | `consultWarehouses` |
| POST | `/api/v1/logistics/orders/{orderId}/shipment/prepare` | `startShipmentPreparation` | - | 201 | `startShipmentPreparation` |
| POST | `/api/v1/logistics/shipments/{shipmentId}/pack` | `packShipment` | - | 200 | `packShipment` |
| POST | `/api/v1/logistics/shipments/{shipmentId}/dispatch` | `dispatchShipment` | `DispatchRequest` | 200 | `dispatchShipment` |
| POST | `/api/v1/logistics/shipments/{shipmentId}/in-transit` | `registerShipmentInTransit` | - | 200 | `registerShipmentInTransit` |
| POST | `/api/v1/logistics/shipments/{shipmentId}/deliver` | `confirmDelivery` | - | 200 | `confirmDelivery` |
| POST | `/api/v1/logistics/returns/{returnId}/receive` | `receiveReturn` | - | 200 | `receiveReturn` |
| GET | `/api/v1/logistics/orders` | `consultOrders` | - | 200 | `consultOrders` |
| GET | `/api/v1/logistics/shipments` | `consultShipments` | - | 200 | `consultShipments` |
| GET | `/api/v1/logistics/orders/{orderId}/shipment` | `consultShipment` | - | 200 | `consultShipment` |
| GET | `/api/v1/logistics/returns` | `consultReturns` | - | 200 | `consultReturns` |
| PUT | `/api/v1/logistics/password` | `changePassword` | `ChangePasswordRequest` | 204 | `changePassword` |

## PublicRestController

Base `/api/v1/public` - access: **Public**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| GET | `/api/v1/public/products` | `consultPublishedProducts` | - | 200 | `consultPublishedProducts` |

## SellerRestController

Base `/api/v1/seller` - access: **SELLER**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| POST | `/api/v1/seller/products` | `RegisterProductCommand` | `RegisterProductRequest` | 201 | `registerProduct` |
| POST | `/api/v1/seller/products/{productId}/publish` | `publishProduct` | - | 200 | `publishProduct` |
| POST | `/api/v1/seller/products/{productId}/variants` | `addProductVariant` | `VariantRequest` | 200 | `addProductVariant` |
| POST | `/api/v1/seller/products/{productId}/suspend` | `suspendProduct` | - | 200 | `suspendProduct` |
| POST | `/api/v1/seller/products/{productId}/discontinue` | `discontinueProduct` | - | 200 | `discontinueProduct` |
| POST | `/api/v1/seller/inventory/entries` | `registerInventoryEntry` | `InventoryEntryRequest` | 201 | `registerInventoryEntry` |
| PUT | `/api/v1/seller/inventory/adjustment` | `adjustInventory` | `InventoryAdjustRequest` | 200 | `adjustInventory` |
| PUT | `/api/v1/seller/inventory/status` | `changeInventoryStatus` | `InventoryStatusRequest` | 200 | `changeInventoryStatus` |
| GET | `/api/v1/seller/inventory/product/{productId}` | `consultInventory` | - | 200 | `consultInventory` |
| GET | `/api/v1/seller/warehouses` | `consultWarehouses` | - | 200 | `consultWarehouses` |
| PUT | `/api/v1/seller/password` | `changePassword` | `ChangePasswordRequest` | 204 | `changePassword` |

## SupervisorRestController

Base `/api/v1/supervisor` - access: **SUPERVISOR**

| Verb | Path | Port operation | Request body | Success | Controller method |
|---|---|---|---|---|---|
| GET | `/api/v1/supervisor/users` | `consultUsers` | - | 200 | `consultUsers` |
| GET | `/api/v1/supervisor/warehouses` | `consultWarehouses` | - | 200 | `consultWarehouses` |
| GET | `/api/v1/supervisor/inventory/product/{productId}` | `consultInventory` | - | 200 | `consultInventory` |
| GET | `/api/v1/supervisor/inventory/movements` | `consultInventoryMovements` | - | 200 | `consultInventoryMovements` |
| GET | `/api/v1/supervisor/orders` | `consultOrders` | - | 200 | `consultOrders` |
| GET | `/api/v1/supervisor/invoices` | `consultInvoices` | - | 200 | `consultInvoices` |
| GET | `/api/v1/supervisor/orders/{orderId}/invoice` | `consultInvoice` | - | 200 | `consultInvoice` |
| GET | `/api/v1/supervisor/shipments` | `consultShipments` | - | 200 | `consultShipments` |
| GET | `/api/v1/supervisor/orders/{orderId}/shipment` | `consultShipment` | - | 200 | `consultShipment` |
| GET | `/api/v1/supervisor/returns` | `consultReturns` | - | 200 | `consultReturns` |
| GET | `/api/v1/supervisor/refunds` | `consultRefunds` | - | 200 | `consultRefunds` |
| GET | `/api/v1/supervisor/reports/sales` | `generateSalesReport` | - | 200 | `generateSalesReport` |
| PUT | `/api/v1/supervisor/password` | `changePassword` | `ChangePasswordRequest` | 204 | `changePassword` |

---

## Response codes

| Code | When |
|---|---|
| 200 / 201 / 204 | Success / created / success without body |
| 400 | Invalid data or malformed JSON |
| 401 | Missing or invalid token, or wrong credentials |
| 403 | Role without permission for the operation |
| 404 | Resource or route not found |
| 405 | HTTP method not supported for the route |
| 409 | Business rule violated (insufficient stock, invalid status transition, duplicate) |
| 500 | Unexpected error |
