package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;

public interface AddItemToCartUseCase {

    Order add(User actor, Long productId, int quantity);
}