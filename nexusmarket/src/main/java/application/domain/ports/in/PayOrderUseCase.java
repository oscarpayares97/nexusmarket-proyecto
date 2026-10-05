package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;

public interface PayOrderUseCase {

    Order pay(User actor, Long orderId, boolean paymentApproved);
}