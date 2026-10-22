package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultMyOrdersService {

    private final OrderRepositoryPort orderRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultMyOrdersService(OrderRepositoryPort orderRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.authorization = authorization;
    }

    public List<Order> consultMyOrders(User actor) {
        authorization.requireRole(actor, UserRole.BUYER);
        return orderRepository.findByBuyerId(actor.getId());
    }
}