package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ConsultShipmentService {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public ConsultShipmentService(ShipmentRepositoryPort shipmentRepository,
                                  OrderRepositoryPort orderRepository,
                                  ValidateRoleAuthorizationService authorization,
                                  ValidateOwnershipService ownership) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    public Shipment consultShipment(User actor, Long orderId) {
        authorization.requireRole(actor, UserRole.BUYER, UserRole.LOGISTICS_OPERATOR,
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        if (actor.getRole() == UserRole.BUYER) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada."));
            ownership.requireOwner(actor, order.getBuyerId());
        }
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("El pedido aún no se ha enviado.."));
    }
}