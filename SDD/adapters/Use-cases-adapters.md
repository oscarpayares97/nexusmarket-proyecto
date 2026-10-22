# Use Case Adapters

Package `application.adapters.useCases`. One class per input port (role). A use case implements the port and **only delegates** to domain services; it has no business logic. Each class is annotated with `@Transactional` so a multi-step operation (for example paying an order: reserve stock, invoice, create shipment) is saved completely or rolled back completely. The classes are registered as beans by `DomainBeansConfig`. Generated from the source code.

---

## AdministratorUseCaseImpl

- **Implements:** `AdministratorPort`
- **Transactional:** yes
- **Injected services:** 22

| Operation | Delegates to |
|---|---|
| `registerSeller` | `RegisterSellerService.register` |
| `registerInternalUser` | `RegisterInternalUserService.register` |
| `changeUserStatus` | `ChangeUserStatusService.change` |
| `changeBuyerCommercialStatus` | `ChangeBuyerCommercialStatusService.change` |
| `createWarehouse` | `CreateWarehouseService.create` |
| `changeWarehouseStatus` | `ChangeWarehouseStatusService.change` |
| `approveReturn` | `ApproveReturnService.approve` |
| `rejectReturn` | `RejectReturnService.reject` |
| `processRefund` | `ProcessRefundService.process` |
| `consultUsers` | `ConsultUserService.consultUsers` |
| `consultWarehouses` | `ConsultWarehousesService.consultWarehouses` |
| `consultInventory` | `ConsultInventoryService.consultInventory` |
| `consultInventoryMovements` | `ConsultInventoryMovementsService.consultMovements` |
| `consultOrders` | `ConsultOrdersService.consultOrders` |
| `consultInvoices` | `ConsultInvoicesService.consultInvoices` |
| `consultInvoice` | `ConsultInvoiceService.consultInvoice` |
| `consultShipments` | `ConsultShipmentsService.consultShipments` |
| `consultShipment` | `ConsultShipmentService.consultShipment` |
| `consultReturns` | `ConsultReturnsService.consultReturns` |
| `consultRefunds` | `ConsultRefundsService.consultRefunds` |
| `generateSalesReport` | `GenerateSalesReportService.generate` |
| `changePassword` | `ChangeUserPasswordService.change` |

---

## BuyerUseCaseImpl

- **Implements:** `BuyerPort`
- **Transactional:** yes
- **Injected services:** 11

| Operation | Delegates to |
|---|---|
| `addAddress` | `AddBuyerAddressService.add` |
| `addItemToCart` | `AddItemToCartService.add` |
| `removeItemFromCart` | `RemoveItemFromCartService.remove` |
| `confirmOrder` | `ConfirmOrderService.confirm` |
| `payOrder` | `PayOrderService.pay` |
| `consultMyOrders` | `ConsultMyOrdersService.consultMyOrders` |
| `consultInvoice` | `ConsultInvoiceService.consultInvoice` |
| `consultShipment` | `ConsultShipmentService.consultShipment` |
| `requestReturn` | `RequestReturnService.request` |
| `consultMyReturns` | `ConsultMyReturnsService.consultMyReturns` |
| `changePassword` | `ChangeUserPasswordService.change` |

---

## LogisticsOperatorUseCaseImpl

- **Implements:** `LogisticsOperatorPort`
- **Transactional:** yes
- **Injected services:** 17

| Operation | Delegates to |
|---|---|
| `registerInventoryEntry` | `RegisterInventoryEntryService.register` |
| `adjustInventory` | `AdjustInventoryService.adjust` |
| `changeInventoryStatus` | `ChangeInventoryStatusService.change` |
| `consultInventory` | `ConsultInventoryService.consultInventory` |
| `consultInventoryMovements` | `ConsultInventoryMovementsService.consultMovements` |
| `consultWarehouses` | `ConsultWarehousesService.consultWarehouses` |
| `startShipmentPreparation` | `StartShipmentPreparationService.start` |
| `packShipment` | `PackShipmentService.pack` |
| `dispatchShipment` | `DispatchShipmentService.dispatch` |
| `registerShipmentInTransit` | `RegisterShipmentInTransitService.register` |
| `confirmDelivery` | `ConfirmDeliveryService.confirm` |
| `receiveReturn` | `ReceiveReturnService.receive` |
| `consultOrders` | `ConsultOrdersService.consultOrders` |
| `consultShipments` | `ConsultShipmentsService.consultShipments` |
| `consultShipment` | `ConsultShipmentService.consultShipment` |
| `consultReturns` | `ConsultReturnsService.consultReturns` |
| `changePassword` | `ChangeUserPasswordService.change` |

---

## PublicAccessUseCaseImpl

- **Implements:** `PublicAccessPort`
- **Transactional:** yes
- **Injected services:** 3

| Operation | Delegates to |
|---|---|
| `registerBuyer` | `RegisterBuyerService.register` |
| `login` | `LoginService.login` |
| `consultPublishedProducts` | `ConsultPublishedProductsService.consult` |

---

## SellerUseCaseImpl

- **Implements:** `SellerPort`
- **Transactional:** yes
- **Injected services:** 11

| Operation | Delegates to |
|---|---|
| `registerProduct` | `RegisterProductService.register` |
| `publishProduct` | `PublishProductService.publish` |
| `addProductVariant` | `AddProductVariantService.add` |
| `suspendProduct` | `SuspendProductService.suspend` |
| `discontinueProduct` | `DiscontinueProductService.discontinue` |
| `registerInventoryEntry` | `RegisterInventoryEntryService.register` |
| `adjustInventory` | `AdjustInventoryService.adjust` |
| `changeInventoryStatus` | `ChangeInventoryStatusService.change` |
| `consultInventory` | `ConsultInventoryService.consultInventory` |
| `consultWarehouses` | `ConsultWarehousesService.consultWarehouses` |
| `changePassword` | `ChangeUserPasswordService.change` |

---

## SupervisorUseCaseImpl

- **Implements:** `SupervisorPort`
- **Transactional:** yes
- **Injected services:** 13

| Operation | Delegates to |
|---|---|
| `consultUsers` | `ConsultUserService.consultUsers` |
| `consultWarehouses` | `ConsultWarehousesService.consultWarehouses` |
| `consultInventory` | `ConsultInventoryService.consultInventory` |
| `consultInventoryMovements` | `ConsultInventoryMovementsService.consultMovements` |
| `consultOrders` | `ConsultOrdersService.consultOrders` |
| `consultInvoices` | `ConsultInvoicesService.consultInvoices` |
| `consultInvoice` | `ConsultInvoiceService.consultInvoice` |
| `consultShipments` | `ConsultShipmentsService.consultShipments` |
| `consultShipment` | `ConsultShipmentService.consultShipment` |
| `consultReturns` | `ConsultReturnsService.consultReturns` |
| `consultRefunds` | `ConsultRefundsService.consultRefunds` |
| `generateSalesReport` | `GenerateSalesReportService.generate` |
| `changePassword` | `ChangeUserPasswordService.change` |

---
