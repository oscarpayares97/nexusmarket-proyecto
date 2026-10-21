package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.User;

import java.util.Map;

public interface BuyerPort {

    Order addItemToCart(User actor, Long productId, int quantity);

    Order confirmOrder(User actor, String deliveryAddress);

    Order payOrder(User actor, Long orderId, boolean paymentApproved);

    ReturnRequest requestReturn(User actor, RequestReturnCommand command);

    record RequestReturnCommand(Long orderId, Map<Long, Integer> quantities, String reason) {
    }
}