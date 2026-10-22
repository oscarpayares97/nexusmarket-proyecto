package application.domain.services.shipping;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentProgressServicesTest {

    private ShipmentRepositoryPort shipments;
    private PackShipmentService pack;
    private RegisterShipmentInTransitService inTransit;
    private final ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();

    private final User oscar =
            new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        shipments = mock(ShipmentRepositoryPort.class);
        when(shipments.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        pack = new PackShipmentService(shipments, authorization);
        inTransit = new RegisterShipmentInTransitService(shipments, authorization);
    }

    @Test
    void packingLeavesThePreparingState() {
        Shipment shipment = new Shipment(20L, 10L, "Street 1");
        when(shipments.findById(20L)).thenReturn(Optional.of(shipment));

        Shipment packed = pack.pack(oscar, 20L);

        assertNotEquals(ShipmentStatus.PREPARING, packed.getStatus());
        verify(shipments).save(shipment);
    }

    @Test
    void aBuyerCannotPackAShipment() {
        assertThrows(UnauthorizedOperationException.class, () -> pack.pack(luis, 20L));
        verify(shipments, never()).save(any(Shipment.class));
    }

    @Test
    void packingAnUnknownShipmentFails() {
        when(shipments.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pack.pack(oscar, 99L));
    }

    @Test
    void aDispatchedShipmentCanBeRegisteredInTransit() {
        Shipment shipment = new Shipment(20L, 10L, "Street 1");
        shipment.pack();
        shipment.dispatch("FastCarrier", "TR-1");
        when(shipments.findById(20L)).thenReturn(Optional.of(shipment));

        Shipment result = inTransit.register(oscar, 20L);

        assertTrue(result.getStatus().name().contains("TRANSIT"));
    }

    @Test
    void aBuyerCannotRegisterTransit() {
        assertThrows(UnauthorizedOperationException.class, () -> inTransit.register(luis, 20L));
    }
}