package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;

public interface LogisticsOperatorPort {

    Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity);

    Shipment startShipmentPreparation(User actor, Long orderId);

    Shipment packShipment(User actor, Long shipmentId);

    Shipment dispatchShipment(User actor, Long shipmentId, String carrier, String trackingNumber);

    Shipment registerShipmentInTransit(User actor, Long shipmentId);

    Shipment confirmDelivery(User actor, Long shipmentId);

    ReturnRequest receiveReturn(User actor, Long returnId);
}