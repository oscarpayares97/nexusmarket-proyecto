package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;

public interface ConfirmOrderUseCase {

    Order confirm(User actor, String deliveryAddress);
}