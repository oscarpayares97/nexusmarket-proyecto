package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class RemoveItemFromCartService {

    private final OrderRepositoryPort orderRepository;
    private final ValidateRoleAuthorizationService authorization;

    public RemoveItemFromCartService(OrderRepositoryPort orderRepository,
                                     ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.authorization = authorization;
    }

    public Order remove(User actor, Long productId) {
        authorization.requireRole(actor, UserRole.BUYER);
        Order cart = orderRepository.findOpenCartByBuyerId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("No hay ningún carrito abierto."));
        cart.removeItem(productId);
        return orderRepository.save(cart);
    }
}