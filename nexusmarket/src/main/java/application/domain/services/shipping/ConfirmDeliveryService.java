package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.ConfirmDeliveryUseCase;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ConfirmDeliveryService implements ConfirmDeliveryUseCase {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConfirmDeliveryService(ShipmentRepositoryPort shipmentRepository,
                                  OrderRepositoryPort orderRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.authorization = authorization;
    }

    @Override
    public Shipment confirm(User actor, Long shipmentId) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Envio no encontrado."));
        shipment.confirmDelivery();

        Order order = orderRepository.findById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        order.markDelivered();
        orderRepository.save(order);
        return shipmentRepository.save(shipment);
    }
}