package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.buyer.AddBuyerAddressService;
import application.domain.services.order.AddItemToCartService;
import application.domain.services.order.ConfirmOrderService;
import application.domain.services.order.ConsultMyOrdersService;
import application.domain.services.order.PayOrderService;
import application.domain.services.order.RemoveItemFromCartService;
import application.domain.services.returns.ConsultMyReturnsService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.shipping.ConsultShipmentService;
import application.domain.services.user.ChangeUserPasswordService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class BuyerUseCaseImpl implements BuyerPort {

    private final AddBuyerAddressService addBuyerAddressService;
    private final AddItemToCartService addItemToCartService;
    private final RemoveItemFromCartService removeItemFromCartService;
    private final ConfirmOrderService confirmOrderService;
    private final PayOrderService payOrderService;
    private final ConsultMyOrdersService consultMyOrdersService;
    private final ConsultInvoiceService consultInvoiceService;
    private final ConsultShipmentService consultShipmentService;
    private final RequestReturnService requestReturnService;
    private final ConsultMyReturnsService consultMyReturnsService;
    private final ChangeUserPasswordService changeUserPasswordService;

    public BuyerUseCaseImpl(AddBuyerAddressService addBuyerAddressService,
                            AddItemToCartService addItemToCartService,
                            RemoveItemFromCartService removeItemFromCartService,
                            ConfirmOrderService confirmOrderService,
                            PayOrderService payOrderService,
                            ConsultMyOrdersService consultMyOrdersService,
                            ConsultInvoiceService consultInvoiceService,
                            ConsultShipmentService consultShipmentService,
                            RequestReturnService requestReturnService,
                            ConsultMyReturnsService consultMyReturnsService,
                            ChangeUserPasswordService changeUserPasswordService) {
        this.addBuyerAddressService = addBuyerAddressService;
        this.addItemToCartService = addItemToCartService;
        this.removeItemFromCartService = removeItemFromCartService;
        this.confirmOrderService = confirmOrderService;
        this.payOrderService = payOrderService;
        this.consultMyOrdersService = consultMyOrdersService;
        this.consultInvoiceService = consultInvoiceService;
        this.consultShipmentService = consultShipmentService;
        this.requestReturnService = requestReturnService;
        this.consultMyReturnsService = consultMyReturnsService;
        this.changeUserPasswordService = changeUserPasswordService;
    }

    @Override
    public Buyer addAddress(User actor, String address) {
        return addBuyerAddressService.add(actor, address);
    }

    @Override
    public Order addItemToCart(User actor, Long productId, int quantity) {
        return addItemToCartService.add(actor, productId, quantity);
    }

    @Override
    public Order removeItemFromCart(User actor, Long productId) {
        return removeItemFromCartService.remove(actor, productId);
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
    public List<Order> consultMyOrders(User actor) {
        return consultMyOrdersService.consultMyOrders(actor);
    }

    @Override
    public Invoice consultInvoice(User actor, Long orderId) {
        return consultInvoiceService.consultInvoice(actor, orderId);
    }

    @Override
    public Shipment consultShipment(User actor, Long orderId) {
        return consultShipmentService.consultShipment(actor, orderId);
    }

    @Override
    public ReturnRequest requestReturn(User actor, RequestReturnCommand command) {
        return requestReturnService.request(actor, command);
    }

    @Override
    public List<ReturnRequest> consultMyReturns(User actor) {
        return consultMyReturnsService.consultMyReturns(actor);
    }

    @Override
    public void changePassword(User actor, String currentPassword, String newPassword) {
        changeUserPasswordService.change(actor, currentPassword, newPassword);
    }
}