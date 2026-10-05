package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.in.IssueInvoiceUseCase;
import application.domain.ports.in.PayOrderUseCase;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.UserRole;

public class PayOrderService implements PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final IssueInvoiceUseCase issueInvoice;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public PayOrderService(OrderRepositoryPort orderRepository,
                           IssueInvoiceUseCase issueInvoice,
                           ValidateRoleAuthorizationService authorization,
                           ValidateOwnershipService ownership) {
        this.orderRepository = orderRepository;
        this.issueInvoice = issueInvoice;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    @Override
    public Order pay(User actor, Long orderId, boolean paymentApproved) {
        authorization.requireRole(actor, UserRole.BUYER);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el pedido."));
        ownership.requireOwner(actor, order.getBuyerId());

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT && !paymentApproved) {
            throw new InvalidDataException("El pago ha sido rechazado. El pedido sigue pendiente de pago..");
        }
        order.markPaid();                 // si el estado no corresponde, lanza el error adecuado
        issueInvoice.issue(order, actor);

        // Entrega inmediata de productos digitales
        if (order.onlyDigital()) {
            order.markShipped();
            order.markDelivered();
        }
        return orderRepository.save(order);
    }
}