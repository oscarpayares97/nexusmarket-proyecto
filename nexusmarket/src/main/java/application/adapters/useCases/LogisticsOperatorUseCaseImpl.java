package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ChangeInventoryStatusService;
import application.domain.services.inventory.ConsultInventoryMovementsService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.shipping.ConfirmDeliveryService;
import application.domain.services.shipping.ConsultShipmentService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.shipping.DispatchShipmentService;
import application.domain.services.shipping.PackShipmentService;
import application.domain.services.shipping.RegisterShipmentInTransitService;
import application.domain.services.shipping.StartShipmentPreparationService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.InventoryStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class LogisticsOperatorUseCaseImpl implements LogisticsOperatorPort {

    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final AdjustInventoryService adjustInventoryService;
    private final ChangeInventoryStatusService changeInventoryStatusService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultInventoryMovementsService consultInventoryMovementsService;
    private final ConsultWarehousesService consultWarehousesService;
    private final StartShipmentPreparationService startShipmentPreparationService;
    private final PackShipmentService packShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final RegisterShipmentInTransitService registerShipmentInTransitService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ReceiveReturnService receiveReturnService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultShipmentService consultShipmentService;
    private final ConsultReturnsService consultReturnsService;
    private final ChangeUserPasswordService changeUserPasswordService;

    public LogisticsOperatorUseCaseImpl(RegisterInventoryEntryService registerInventoryEntryService,
                                        AdjustInventoryService adjustInventoryService,
                                        ChangeInventoryStatusService changeInventoryStatusService,
                                        ConsultInventoryService consultInventoryService,
                                        ConsultInventoryMovementsService consultInventoryMovementsService,
                                        ConsultWarehousesService consultWarehousesService,
                                        StartShipmentPreparationService startShipmentPreparationService,
                                        PackShipmentService packShipmentService,
                                        DispatchShipmentService dispatchShipmentService,
                                        RegisterShipmentInTransitService registerShipmentInTransitService,
                                        ConfirmDeliveryService confirmDeliveryService,
                                        ReceiveReturnService receiveReturnService,
                                        ConsultOrdersService consultOrdersService,
                                        ConsultShipmentsService consultShipmentsService,
                                        ConsultShipmentService consultShipmentService,
                                        ConsultReturnsService consultReturnsService,
                                        ChangeUserPasswordService changeUserPasswordService) {
        this.registerInventoryEntryService = registerInventoryEntryService;
        this.adjustInventoryService = adjustInventoryService;
        this.changeInventoryStatusService = changeInventoryStatusService;
        this.consultInventoryService = consultInventoryService;
        this.consultInventoryMovementsService = consultInventoryMovementsService;
        this.consultWarehousesService = consultWarehousesService;
        this.startShipmentPreparationService = startShipmentPreparationService;
        this.packShipmentService = packShipmentService;
        this.dispatchShipmentService = dispatchShipmentService;
        this.registerShipmentInTransitService = registerShipmentInTransitService;
        this.confirmDeliveryService = confirmDeliveryService;
        this.receiveReturnService = receiveReturnService;
        this.consultOrdersService = consultOrdersService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultShipmentService = consultShipmentService;
        this.consultReturnsService = consultReturnsService;
        this.changeUserPasswordService = changeUserPasswordService;
    }

    @Override
    public Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity) {
        return registerInventoryEntryService.register(actor, productId, warehouseId, quantity);
    }

    @Override
    public Inventory adjustInventory(User actor, Long productId, Long warehouseId, int newQuantity) {
        return adjustInventoryService.adjust(actor, productId, warehouseId, newQuantity);
    }

    @Override
    public Inventory changeInventoryStatus(User actor, Long productId, Long warehouseId,
                                           InventoryStatus status) {
        return changeInventoryStatusService.change(actor, productId, warehouseId, status);
    }

    @Override
    public List<Inventory> consultInventory(User actor, Long productId) {
        return consultInventoryService.consultInventory(actor, productId);
    }

    @Override
    public List<InventoryMovement> consultInventoryMovements(User actor) {
        return consultInventoryMovementsService.consultMovements(actor);
    }

    @Override
    public List<Warehouse> consultWarehouses(User actor) {
        return consultWarehousesService.consultWarehouses(actor);
    }

    @Override
    public Shipment startShipmentPreparation(User actor, Long orderId) {
        return startShipmentPreparationService.start(actor, orderId);
    }

    @Override
    public Shipment packShipment(User actor, Long shipmentId) {
        return packShipmentService.pack(actor, shipmentId);
    }

    @Override
    public Shipment dispatchShipment(User actor, Long shipmentId, String carrier, String trackingNumber) {
        return dispatchShipmentService.dispatch(actor, shipmentId, carrier, trackingNumber);
    }

    @Override
    public Shipment registerShipmentInTransit(User actor, Long shipmentId) {
        return registerShipmentInTransitService.register(actor, shipmentId);
    }

    @Override
    public Shipment confirmDelivery(User actor, Long shipmentId) {
        return confirmDeliveryService.confirm(actor, shipmentId);
    }

    @Override
    public ReturnRequest receiveReturn(User actor, Long returnId) {
        return receiveReturnService.receive(actor, returnId);
    }

    @Override
    public List<Order> consultOrders(User actor) {
        return consultOrdersService.consultOrders(actor);
    }

    @Override
    public List<Shipment> consultShipments(User actor) {
        return consultShipmentsService.consultShipments(actor);
    }

    @Override
    public Shipment consultShipment(User actor, Long orderId) {
        return consultShipmentService.consultShipment(actor, orderId);
    }

    @Override
    public List<ReturnRequest> consultReturns(User actor) {
        return consultReturnsService.consultReturns(actor);
    }

    @Override
    public void changePassword(User actor, String currentPassword, String newPassword) {
        changeUserPasswordService.change(actor, currentPassword, newPassword);
    }
}