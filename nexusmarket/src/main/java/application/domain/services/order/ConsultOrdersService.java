package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultOrdersService {

    private final OrderRepositoryPort orderRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultOrdersService(OrderRepositoryPort orderRepository,
                                ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.authorization = authorization;
    }

    public List<Order> consultOrders(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR,
                UserRole.LOGISTICS_OPERATOR);
        return orderRepository.findAll();
    }
}