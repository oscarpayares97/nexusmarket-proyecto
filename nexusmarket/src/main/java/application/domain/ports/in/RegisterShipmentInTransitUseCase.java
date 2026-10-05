package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

public interface RegisterShipmentInTransitUseCase {

    Shipment register(User actor, Long shipmentId);
}