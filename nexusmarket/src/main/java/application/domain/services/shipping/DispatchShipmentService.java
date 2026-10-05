package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.DispatchShipmentUseCase;
import application.domain.ports.in.RegisterInventorySaleUseCase;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class DispatchShipmentService implements DispatchShipmentUseCase {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final RegisterInventorySaleUseCase inventorySale;
    private final ValidateRoleAuthorizationService authorization;

    public DispatchShipmentService(ShipmentRepositoryPort shipmentRepository,
                                   OrderRepositoryPort orderRepository,
                                   RegisterInventorySaleUseCase inventorySale,
                                   ValidateRoleAuthorizationService authorization) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.inventorySale = inventorySale;
        this.authorization = authorization;
    }

    @Override
    public Shipment dispatch(User actor, Command command) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        Shipment shipment = shipmentRepository.findById(command.shipmentId())
                .orElseThrow(() -> new EntityNotFoundException("Envio no encontrado."));
        shipment.dispatch(command.carrier(), command.trackingNumber());   // valida estado y datos

        Order order = orderRepository.findById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        order.markShipped();

        for (OrderItem item : order.getItems()) {
            if (item.isPhysical()) {
                inventorySale.registerSale(actor, item.getProductId(), item.getWarehouseId(), item.getQuantity());
            }
        }
        orderRepository.save(order);
        return shipmentRepository.save(shipment);
    }
}