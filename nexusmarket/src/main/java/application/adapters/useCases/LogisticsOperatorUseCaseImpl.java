package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.shipping.ConfirmDeliveryService;
import application.domain.services.shipping.DispatchShipmentService;
import application.domain.services.shipping.PackShipmentService;
import application.domain.services.shipping.RegisterShipmentInTransitService;
import application.domain.services.shipping.StartShipmentPreparationService;

public class LogisticsOperatorUseCaseImpl implements LogisticsOperatorPort {

    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final StartShipmentPreparationService startShipmentPreparationService;
    private final PackShipmentService packShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final RegisterShipmentInTransitService registerShipmentInTransitService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ReceiveReturnService receiveReturnService;

    public LogisticsOperatorUseCaseImpl(RegisterInventoryEntryService registerInventoryEntryService,
                                        StartShipmentPreparationService startShipmentPreparationService,
                                        PackShipmentService packShipmentService,
                                        DispatchShipmentService dispatchShipmentService,
                                        RegisterShipmentInTransitService registerShipmentInTransitService,
                                        ConfirmDeliveryService confirmDeliveryService,
                                        ReceiveReturnService receiveReturnService) {
        this.registerInventoryEntryService = registerInventoryEntryService;
        this.startShipmentPreparationService = startShipmentPreparationService;
        this.packShipmentService = packShipmentService;
        this.dispatchShipmentService = dispatchShipmentService;
        this.registerShipmentInTransitService = registerShipmentInTransitService;
        this.confirmDeliveryService = confirmDeliveryService;
        this.receiveReturnService = receiveReturnService;
    }

    @Override
    public Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity) {
        return registerInventoryEntryService.register(actor, productId, warehouseId, quantity);
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
}