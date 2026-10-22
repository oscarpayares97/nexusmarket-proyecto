package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryStatus;

import java.util.List;

public interface LogisticsOperatorPort {

    Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity);

    Inventory adjustInventory(User actor, Long productId, Long warehouseId, int newQuantity);

    Inventory changeInventoryStatus(User actor, Long productId, Long warehouseId, InventoryStatus status);

    List<Inventory> consultInventory(User actor, Long productId);

    List<InventoryMovement> consultInventoryMovements(User actor);

    List<Warehouse> consultWarehouses(User actor);

    Shipment startShipmentPreparation(User actor, Long orderId);

    Shipment packShipment(User actor, Long shipmentId);

    Shipment dispatchShipment(User actor, Long shipmentId, String carrier, String trackingNumber);

    Shipment registerShipmentInTransit(User actor, Long shipmentId);

    Shipment confirmDelivery(User actor, Long shipmentId);

    ReturnRequest receiveReturn(User actor, Long returnId);

    List<Order> consultOrders(User actor);

    List<Shipment> consultShipments(User actor);

    Shipment consultShipment(User actor, Long orderId);

    List<ReturnRequest> consultReturns(User actor);

    void changePassword(User actor, String currentPassword, String newPassword);
}