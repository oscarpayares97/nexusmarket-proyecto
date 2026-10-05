package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

public interface ConfirmDeliveryUseCase {

    Shipment confirm(User actor, Long shipmentId);
}