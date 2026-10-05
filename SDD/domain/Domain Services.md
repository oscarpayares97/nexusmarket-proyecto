# Domain Services: NexusMarket

Cada servicio implementa un puerto de entrada (`domain/ports/in`) y usa puertos de salida (`domain/ports/out`).
Todos reciben el `User actor` y validan rol y propiedad antes de operar (RG-01, RG-03).

| Módulo | Servicio | Rol que lo ejecuta | Reglas principales | Excepciones |
|---|---|---|---|---|
| authorization | ValidateRoleAuthorizationService | Todos | Usuario existente, activo y con rol permitido | UnauthorizedOperationException |
| authorization | ValidateOwnershipService | Todos | Solo el dueño opera sus recursos | UnauthorizedOperationException |
| buyer | RegisterBuyerService | Público | Documento y correo únicos; contraseña de 8 o más caracteres; dirección principal obligatoria | DuplicateEntityException, InvalidDataException |
| seller | RegisterSellerService | Administrador | Registra al vendedor y su primera bodega; los vendedores no se auto-registran | UnauthorizedOperationException, DuplicateEntityException |
| warehouse | CreateWarehouseService | Administrador | Sin vendedor la bodega es del Marketplace; con vendedor debe existir | EntityNotFoundException |
| catalog | RegisterProductService | Vendedor | Producto nace SUSPENDED; precio mayor que cero | InvalidDataException |
| catalog | PublishProductService | Vendedor dueño | Solo el dueño publica; un producto DISCONTINUED no se publica | UnauthorizedOperationException |
| catalog | ConsultPublishedProductsService | Público | Devuelve solo productos PUBLISHED | n/a |
| inventory | RegisterInventoryEntryService | Vendedor / Operador | Solo productos físicos; bodega activa; el vendedor opera solo lo suyo | InvalidDataException, UnauthorizedOperationException |
| inventory | ReserveInventoryService | Sistema (al confirmar pedido) | Nunca existencias negativas; no reserva inventario DAMAGED | InsufficientStockException |
| inventory | RegisterInventorySaleService | Sistema (al despachar) | La venta consume unidades reservadas | InsufficientStockException |
| inventory | RegisterInventoryReturnService | Sistema (al recibir devolución) | Las unidades vuelven a disponibles | EntityNotFoundException |
| order | AddItemToCartService | Comprador | Solo productos PUBLISHED; un carrito abierto por comprador | InvalidDataException |
| order | ConfirmOrderService | Comprador | Comprador habilitado; dirección registrada; valida todo antes de reservar | InsufficientStockException, UnauthorizedOperationException |
| order | PayOrderService | Comprador dueño | Pago aprobado; emite factura; pedido solo digital se entrega de inmediato | InvalidDataException |
| billing | IssueInvoiceService | Sistema (al pagar) | Una factura por pedido; inmutable | DuplicateEntityException |
| shipping | StartShipmentPreparationService | Operador logístico | Pedido PAID con productos físicos; un envío por pedido | InvalidStatusTransitionException |
| shipping | PackShipmentService | Operador logístico | PREPARING a PACKED | InvalidStatusTransitionException |
| shipping | DispatchShipmentService | Operador logístico | Transportadora y guía obligatorias; pedido a SHIPPED; salida por venta | InvalidDataException |
| shipping | RegisterShipmentInTransitService | Operador logístico | SHIPPED a IN_TRANSIT | InvalidStatusTransitionException |
| shipping | ConfirmDeliveryService | Operador logístico | Pedido a DELIVERED; ya no se puede modificar | InvalidStatusTransitionException |
| returns | RequestReturnService | Comprador dueño | Solo pedidos DELIVERED; solo físicos; nunca más unidades de las compradas | InvalidDataException |
| returns | ApproveReturnService / RejectReturnService | Administrador | Solo desde REQUESTED; el rechazo exige motivo | InvalidStatusTransitionException |
| returns | ReceiveReturnService | Operador logístico | Solo devoluciones APPROVED; reingresa inventario y crea el reembolso | InvalidStatusTransitionException |
| returns | ProcessRefundService | Administrador | Un reembolso se procesa una sola vez | InvalidStatusTransitionException |