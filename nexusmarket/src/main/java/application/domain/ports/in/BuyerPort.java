package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;

import java.util.List;
import java.util.Map;

public interface BuyerPort {

    Buyer addAddress(User actor, String address);

    Order addItemToCart(User actor, Long productId, int quantity);

    Order removeItemFromCart(User actor, Long productId);

    Order confirmOrder(User actor, String deliveryAddress);

    Order payOrder(User actor, Long orderId, boolean paymentApproved);

    List<Order> consultMyOrders(User actor);

    Invoice consultInvoice(User actor, Long orderId);

    Shipment consultShipment(User actor, Long orderId);

    ReturnRequest requestReturn(User actor, RequestReturnCommand command);

    List<ReturnRequest> consultMyReturns(User actor);

    void changePassword(User actor, String currentPassword, String newPassword);

    record RequestReturnCommand(Long orderId, Map<Long, Integer> quantities, String reason) {
    }
}