# Input Ports: NexusMarket

Paquete `application.domain.ports.in`. Un puerto de entrada por rol; cada puerto declara las
operaciones que ese rol puede ejecutar. Los implementan las clases `UseCaseImpl` de
`application.adapters.useCases`, que solo delegan en los servicios de dominio. Las operaciones reciben
el `User actor` (usuario autenticado) salvo las públicas.

Cada `UseCaseImpl` solo delega en servicios de dominio y se ejecuta dentro de una transacción
(`@Transactional`). La autorización por rol la valida cada servicio con
`ValidateRoleAuthorizationService`, y la capa REST la refuerza en `SecurityConfig`.

## Resumen

| Puerto | Rol | Operaciones |
|---|---|---|
| PublicAccessPort | Sin sesión | 3 |
| BuyerPort | BUYER | 11 |
| SellerPort | SELLER | 11 |
| LogisticsOperatorPort | LOGISTICS_OPERATOR | 17 |
| AdministratorPort | ADMINISTRATOR | 22 |
| SupervisorPort | SUPERVISOR (solo consulta) | 13 |

## PublicAccessPort (sin sesión)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerBuyer | RegisterBuyerService | RegisterBuyerCommand |
| login | LoginService | email, rawPassword; devuelve AuthenticationResult |
| consultPublishedProducts | ConsultPublishedProductsService | n/a |

## BuyerPort (rol BUYER)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| addAddress | AddBuyerAddressService | address |
| addItemToCart | AddItemToCartService | productId, quantity |
| removeItemFromCart | RemoveItemFromCartService | productId |
| confirmOrder | ConfirmOrderService | deliveryAddress |
| payOrder | PayOrderService | orderId, paymentApproved |
| consultMyOrders | ConsultMyOrdersService | n/a |
| consultInvoice | ConsultInvoiceService | orderId |
| consultShipment | ConsultShipmentService | orderId |
| requestReturn | RequestReturnService | RequestReturnCommand |
| consultMyReturns | ConsultMyReturnsService | n/a |
| changePassword | ChangeUserPasswordService | currentPassword, newPassword |

## SellerPort (rol SELLER)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerProduct | RegisterProductService | RegisterProductCommand |
| publishProduct | PublishProductService | productId |
| addProductVariant | AddProductVariantService | productId, variant |
| suspendProduct | SuspendProductService | productId |
| discontinueProduct | DiscontinueProductService | productId |
| registerInventoryEntry | RegisterInventoryEntryService | productId, warehouseId, quantity |
| adjustInventory | AdjustInventoryService | productId, warehouseId, newQuantity |
| changeInventoryStatus | ChangeInventoryStatusService | productId, warehouseId, status |
| consultInventory | ConsultInventoryService | productId |
| consultWarehouses | ConsultWarehousesService | n/a |
| changePassword | ChangeUserPasswordService | currentPassword, newPassword |

## LogisticsOperatorPort (rol LOGISTICS_OPERATOR)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerInventoryEntry | RegisterInventoryEntryService | productId, warehouseId, quantity |
| adjustInventory | AdjustInventoryService | productId, warehouseId, newQuantity |
| changeInventoryStatus | ChangeInventoryStatusService | productId, warehouseId, status |
| consultInventory | ConsultInventoryService | productId |
| consultInventoryMovements | ConsultInventoryMovementsService | n/a |
| consultWarehouses | ConsultWarehousesService | n/a |
| startShipmentPreparation | StartShipmentPreparationService | orderId |
| packShipment | PackShipmentService | shipmentId |
| dispatchShipment | DispatchShipmentService | shipmentId, carrier, trackingNumber |
| registerShipmentInTransit | RegisterShipmentInTransitService | shipmentId |
| confirmDelivery | ConfirmDeliveryService | shipmentId |
| receiveReturn | ReceiveReturnService | returnId |
| consultOrders | ConsultOrdersService | n/a |
| consultShipments | ConsultShipmentsService | n/a |
| consultShipment | ConsultShipmentService | orderId |
| consultReturns | ConsultReturnsService | n/a |
| changePassword | ChangeUserPasswordService | currentPassword, newPassword |

## AdministratorPort (rol ADMINISTRATOR)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerSeller | RegisterSellerService | RegisterSellerCommand |
| registerInternalUser | RegisterInternalUserService | RegisterInternalUserCommand |
| changeUserStatus | ChangeUserStatusService | userId, newStatus |
| changeBuyerCommercialStatus | ChangeBuyerCommercialStatusService | buyerUserId, newStatus |
| createWarehouse | CreateWarehouseService | CreateWarehouseCommand |
| changeWarehouseStatus | ChangeWarehouseStatusService | warehouseId, active |
| approveReturn | ApproveReturnService | returnId |
| rejectReturn | RejectReturnService | returnId, reason |
| processRefund | ProcessRefundService | refundId |
| consultUsers | ConsultUserService | n/a |
| consultWarehouses | ConsultWarehousesService | n/a |
| consultInventory | ConsultInventoryService | productId |
| consultInventoryMovements | ConsultInventoryMovementsService | n/a |
| consultOrders | ConsultOrdersService | n/a |
| consultInvoices | ConsultInvoicesService | n/a |
| consultInvoice | ConsultInvoiceService | orderId |
| consultShipments | ConsultShipmentsService | n/a |
| consultShipment | ConsultShipmentService | orderId |
| consultReturns | ConsultReturnsService | n/a |
| consultRefunds | ConsultRefundsService | n/a |
| generateSalesReport | GenerateSalesReportService | n/a |
| changePassword | ChangeUserPasswordService | currentPassword, newPassword |

## SupervisorPort (rol SUPERVISOR, solo consulta)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| consultUsers | ConsultUserService | n/a |
| consultWarehouses | ConsultWarehousesService | n/a |
| consultInventory | ConsultInventoryService | productId |
| consultInventoryMovements | ConsultInventoryMovementsService | n/a |
| consultOrders | ConsultOrdersService | n/a |
| consultInvoices | ConsultInvoicesService | n/a |
| consultInvoice | ConsultInvoiceService | orderId |
| consultShipments | ConsultShipmentsService | n/a |
| consultShipment | ConsultShipmentService | orderId |
| consultReturns | ConsultReturnsService | n/a |
| consultRefunds | ConsultRefundsService | n/a |
| generateSalesReport | GenerateSalesReportService | n/a |
| changePassword | ChangeUserPasswordService | currentPassword, newPassword |

## Servicios sin puerto de rol
| Servicio | Quién lo usa |
|---|---|
| ReserveInventoryService, RegisterInventorySaleService, RegisterInventoryReturnService, IssueInvoiceService | Otros servicios del dominio (al confirmar, despachar, recibir una devolución y pagar) |
| LoadAuthenticatedUserService | El filtro JWT de la capa de seguridad |
| ValidateRoleAuthorizationService | Los demás servicios, para validar el rol del actor |

## Decisiones
- **No hay LogoutService.** Con JWT sin estado el cliente descarta el token; el servidor no guarda sesiones.
- **Transacciones.** Cada UseCaseImpl lleva `@Transactional`: una operación de varios pasos (por ejemplo pagar: reservar stock, facturar y crear el envío) se guarda completa o se deshace completa.
- **Autorización en dos capas.** Los servicios validan el rol del actor (dominio) y `SecurityConfig` restringe las rutas por rol (REST).