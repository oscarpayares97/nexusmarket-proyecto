package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

public interface StartShipmentPreparationUseCase {

    Shipment start(User actor, Long orderId);
}