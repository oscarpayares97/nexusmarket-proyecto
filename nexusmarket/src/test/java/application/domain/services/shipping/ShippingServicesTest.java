package application.domain.services.shipping;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.DispatchShipmentUseCase;
import application.domain.ports.in.RegisterInventorySaleUseCase;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShippingServicesTest {

    private OrderRepositoryPort orders;
    private ShipmentRepositoryPort shipments;
    private RegisterInventorySaleUseCase inventorySale;
    private final ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
    private Order paidOrder;
    private Product shirt;

    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        orders = mock(OrderRepositoryPort.class);
        shipments = mock(ShipmentRepositoryPort.class);
        inventorySale = mock(RegisterInventorySaleUseCase.class);

        shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        paidOrder = new Order(null, 5L);
        paidOrder.assignId(10L);
        paidOrder.addItem(shirt, 3);
        paidOrder.getItems().get(0).assignWarehouse(7L);
        paidOrder.confirm("Street 1");
        paidOrder.markPaid();

        when(orders.findById(10L)).thenReturn(Optional.of(paidOrder));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(shipments.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void startingPreparationCreatesAShipmentForAPaidOrder() {
        Shipment shipment = new StartShipmentPreparationService(orders, shipments, authorization).start(oscar, 10L);
        assertEquals(ShipmentStatus.PREPARING, shipment.getStatus());
        assertEquals("Street 1", shipment.getDeliveryAddress());
    }

    @Test
    void cannotPrepareAnOrderThatIsNotPaid() {
        Order unpaid = new Order(null, 5L);
        unpaid.assignId(11L);
        unpaid.addItem(shirt, 1);
        unpaid.confirm("Street 1");
        when(orders.findById(11L)).thenReturn(Optional.of(unpaid));
        StartShipmentPreparationService service = new StartShipmentPreparationService(orders, shipments, authorization);
        assertThrows(InvalidStatusTransitionException.class, () -> service.start(oscar, 11L));
    }

    @Test
    void cannotPrepareADigitalOnlyOrder() {
        Product ebook = new Product(2L, 2L, "Ebook", ProductType.DIGITAL, Money.of("50000"));
        Order digital = new Order(null, 5L);
        digital.assignId(12L);
        digital.addItem(ebook, 1);
        digital.confirm("Street 1");
        digital.markPaid();
        when(orders.findById(12L)).thenReturn(Optional.of(digital));
        StartShipmentPreparationService service = new StartShipmentPreparationService(orders, shipments, authorization);
        assertThrows(InvalidDataException.class, () -> service.start(oscar, 12L));
    }

    @Test
    void dispatchMarksTheOrderAsShippedAndRegistersTheSale() {
        Shipment shipment = new Shipment(20L, 10L, "Street 1");
        shipment.pack();
        when(shipments.findById(20L)).thenReturn(Optional.of(shipment));

        new DispatchShipmentService(shipments, orders, inventorySale, authorization)
                .dispatch(oscar, new DispatchShipmentUseCase.Command(20L, "FastCarrier", "TR-1"));

        assertEquals(OrderStatus.SHIPPED, paidOrder.getStatus());
        verify(inventorySale).registerSale(oscar, 1L, 7L, 3);
    }

    @Test
    void confirmingTheDeliveryCompletesTheOrder() {
        Shipment shipment = new Shipment(20L, 10L, "Street 1");
        shipment.pack();
        shipment.dispatch("FastCarrier", "TR-1");
        shipment.markInTransit();
        paidOrder.markShipped();
        when(shipments.findById(20L)).thenReturn(Optional.of(shipment));

        new ConfirmDeliveryService(shipments, orders, authorization).confirm(oscar, 20L);

        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertEquals(OrderStatus.DELIVERED, paidOrder.getStatus());
    }

    @Test
    void aBuyerCannotDispatchAShipment() {
        DispatchShipmentService service = new DispatchShipmentService(shipments, orders, inventorySale, authorization);
        assertThrows(UnauthorizedOperationException.class,
                () -> service.dispatch(luis, new DispatchShipmentUseCase.Command(20L, "FastCarrier", "TR-1")));
    }
}