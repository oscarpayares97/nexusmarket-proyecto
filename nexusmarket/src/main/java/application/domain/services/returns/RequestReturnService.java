package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import application.domain.ports.in.BuyerPort.RequestReturnCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestReturnService {

    private final OrderRepositoryPort orderRepository;
    private final ReturnRepositoryPort returnRepository;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public RequestReturnService(OrderRepositoryPort orderRepository,
                                ReturnRepositoryPort returnRepository,
                                ValidateRoleAuthorizationService authorization,
                                ValidateOwnershipService ownership) {
        this.orderRepository = orderRepository;
        this.returnRepository = returnRepository;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    public ReturnRequest request(User actor, RequestReturnCommand command) {
        authorization.requireRole(actor, UserRole.BUYER);
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        ownership.requireOwner(actor, order.getBuyerId());

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException("Solo se pueden devolver los pedidos que ya se hayan entregado.");
        }
        if (command.quantities() == null || command.quantities().isEmpty()) {
            throw new InvalidDataException("Se debe devolver al menos un producto.");
        }

        List<ReturnItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : command.quantities().entrySet()) {
            OrderItem original = findItem(order, entry.getKey());
            int inProcess = quantityInProcess(order.getId(), entry.getKey());
            int available = original.getQuantity() - inProcess;
            if (entry.getValue() > available) {
                throw new InvalidDataException("Solo " + available + " unidades de  '"
                        + original.getProductName() + "' se puede devolver (comprado: "
                        + original.getQuantity() + ", ya devueltos o en trámite: " + inProcess + ").");
            }
            items.add(ReturnItem.from(original, entry.getValue()));
        }
        return returnRepository.save(
                new ReturnRequest(null, order.getId(), actor.getId(), items, command.reason()));
    }

    private OrderItem findItem(Order order, Long productId) {
        for (OrderItem item : order.getItems()) {
            if (Objects.equals(item.getProductId(), productId)) {
                return item;
            }
        }
        throw new InvalidDataException("The product " + productId + " is not part of the order.");
    }

    // Unidades de ese producto ya devueltas o en trámite (las rechazadas no cuentan)
    private int quantityInProcess(Long orderId, Long productId) {
        int total = 0;
        for (ReturnRequest existing : returnRepository.findByOrderId(orderId)) {
            if (existing.getStatus() != ReturnStatus.REJECTED) {
                for (ReturnItem item : existing.getItems()) {
                    if (Objects.equals(item.getProductId(), productId)) {
                        total += item.getQuantity();
                    }
                }
            }
        }
        return total;
    }
}