package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

public interface DispatchShipmentUseCase {

    Shipment dispatch(User actor, Command command);

    record Command(Long shipmentId, String carrier, String trackingNumber) {
    }
}