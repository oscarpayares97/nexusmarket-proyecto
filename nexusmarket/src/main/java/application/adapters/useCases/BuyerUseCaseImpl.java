package application.adapters.useCases;

import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.services.order.AddItemToCartService;
import application.domain.services.order.ConfirmOrderService;
import application.domain.services.order.PayOrderService;
import application.domain.services.returns.RequestReturnService;

public class BuyerUseCaseImpl implements BuyerPort {

    private final AddItemToCartService addItemToCartService;
    private final ConfirmOrderService confirmOrderService;
    private final PayOrderService payOrderService;
    private final RequestReturnService requestReturnService;

    public BuyerUseCaseImpl(AddItemToCartService addItemToCartService,
                            ConfirmOrderService confirmOrderService,
                            PayOrderService payOrderService,
                            RequestReturnService requestReturnService) {
        this.addItemToCartService = addItemToCartService;
        this.confirmOrderService = confirmOrderService;
        this.payOrderService = payOrderService;
        this.requestReturnService = requestReturnService;
    }

    @Override
    public Order addItemToCart(User actor, Long productId, int quantity) {
        return addItemToCartService.add(actor, productId, quantity);
    }

    @Override
    public Order confirmOrder(User actor, String deliveryAddress) {
        return confirmOrderService.confirm(actor, deliveryAddress);
    }

    @Override
    public Order payOrder(User actor, Long orderId, boolean paymentApproved) {
        return payOrderService.pay(actor, orderId, paymentApproved);
    }

    @Override
    public ReturnRequest requestReturn(User actor, RequestReturnCommand command) {
        return requestReturnService.request(actor, command);
    }
}