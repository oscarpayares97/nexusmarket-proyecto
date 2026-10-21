package application.domain.services.shipping;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.UserRole;

public class StartShipmentPreparationService {

    private final OrderRepositoryPort orderRepository;
    private final ShipmentRepositoryPort shipmentRepository;
    private final ValidateRoleAuthorizationService authorization;

    public StartShipmentPreparationService(OrderRepositoryPort orderRepository,
                                           ShipmentRepositoryPort shipmentRepository,
                                           ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.authorization = authorization;
    }

    public Shipment start(User actor, Long orderId) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el pedido."));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidStatusTransitionException(
                    "Solo se puede tramitar un pedido pagado. Estado actual: " + order.getStatus() + ".");
        }
        if (order.onlyDigital()) {
            throw new InvalidDataException("Los pedidos exclusivamente digitales no requieren gastos de envío.");
        }
        if (shipmentRepository.findByOrderId(orderId).isPresent()) {
            throw new DuplicateEntityException("El pedido ya se ha enviado.");
        }
        return shipmentRepository.save(new Shipment(null, orderId, order.getDeliveryAddress()));
    }
}