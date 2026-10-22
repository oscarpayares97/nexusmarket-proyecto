package application.domain.services.shipping;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShipmentQueryServicesTest {

    private ConsultShipmentService consultOne;
    private ConsultShipmentsService consultAll;
    private Shipment shipment;

    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User sara = new User(6L, "1004", "Sara Diaz", "sara@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        ShipmentRepositoryPort shipments = mock(ShipmentRepositoryPort.class);
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);

        shipment = new Shipment(20L, 10L, "Street 1");
        when(shipments.findByOrderId(10L)).thenReturn(Optional.of(shipment));
        when(shipments.findAll()).thenReturn(List.of(shipment));
        when(orders.findById(10L)).thenReturn(Optional.of(new Order(10L, 5L)));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        consultOne = new ConsultShipmentService(shipments, orders, authorization, new ValidateOwnershipService());
        consultAll = new ConsultShipmentsService(shipments, authorization);
    }

    @Test
    void theOwnerTracksTheShipment() {
        assertEquals(shipment, consultOne.consultShipment(luis, 10L));
    }

    @Test
    void anotherBuyerCannotTrackTheShipment() {
        assertThrows(UnauthorizedOperationException.class, () -> consultOne.consultShipment(sara, 10L));
    }

    @Test
    void theOperatorListsAllTheShipments() {
        assertEquals(1, consultAll.consultShipments(oscar).size());
    }
}