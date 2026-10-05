package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

public interface PackShipmentUseCase {

    Shipment pack(User actor, Long shipmentId);
}