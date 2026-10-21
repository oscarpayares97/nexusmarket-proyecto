package application.domain.services.shipping;

import application.domain.services.inventory.RegisterInventorySaleService;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class DispatchShipmentService {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final RegisterInventorySaleService inventorySale;
    private final ValidateRoleAuthorizationService authorization;

    public DispatchShipmentService(ShipmentRepositoryPort shipmentRepository,
                                   OrderRepositoryPort orderRepository,
                                   RegisterInventorySaleService inventorySale,
                                   ValidateRoleAuthorizationService authorization) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.inventorySale = inventorySale;
        this.authorization = authorization;
    }

     public Shipment dispatch(User actor, Long shipmentId, String carrier, String trackingNumber) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Envio no encontrado."));
        shipment.dispatch(carrier, trackingNumber);   // valida estado y datos

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