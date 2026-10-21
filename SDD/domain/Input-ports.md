# Input Ports: NexusMarket

Paquete `application.domain.ports.in`. Un puerto de entrada por rol; cada puerto declara las
operaciones que ese rol puede ejecutar. Los implementan las clases `UseCaseImpl` de
`application.adapters.useCases`, que delegan en los servicios de dominio. Las operaciones reciben
el `User actor` (el usuario autenticado) salvo las públicas.

## PublicAccessPort (sin sesión)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerBuyer | RegisterBuyerService | RegisterBuyerCommand |
| consultPublishedProducts | ConsultPublishedProductsService | n/a |

## BuyerPort (rol BUYER)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| addItemToCart | AddItemToCartService | productId, quantity |
| confirmOrder | ConfirmOrderService | deliveryAddress |
| payOrder | PayOrderService | orderId, paymentApproved |
| requestReturn | RequestReturnService | RequestReturnCommand |

## SellerPort (rol SELLER)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerProduct | RegisterProductService | RegisterProductCommand |
| publishProduct | PublishProductService | productId |
| registerInventoryEntry | RegisterInventoryEntryService | productId, warehouseId, quantity |

## LogisticsOperatorPort (rol LOGISTICS_OPERATOR)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerInventoryEntry | RegisterInventoryEntryService | productId, warehouseId, quantity |
| startShipmentPreparation | StartShipmentPreparationService | orderId |
| packShipment | PackShipmentService | shipmentId |
| dispatchShipment | DispatchShipmentService | shipmentId, carrier, trackingNumber |
| registerShipmentInTransit | RegisterShipmentInTransitService | shipmentId |
| confirmDelivery | ConfirmDeliveryService | shipmentId |
| receiveReturn | ReceiveReturnService | returnId |

## AdministratorPort (rol ADMINISTRATOR)
| Operación | Servicio de dominio | Entrada |
|---|---|---|
| registerSeller | RegisterSellerService | RegisterSellerCommand |
| createWarehouse | CreateWarehouseService | CreateWarehouseCommand |
| approveReturn | ApproveReturnService | returnId |
| rejectReturn | RejectReturnService | returnId, reason |
| processRefund | ProcessRefundService | refundId |

## Servicios internos (sin puerto de rol)
Los usan otros servicios del dominio, no los roles directamente: ReserveInventoryService (al confirmar un
pedido), RegisterInventorySaleService (al despachar), RegisterInventoryReturnService (al recibir una
devolución) e IssueInvoiceService (al pagar un pedido).

## Pendiente
SupervisorPort (rol de consulta), login, cambio de contraseña, administración de usuarios y consultas.