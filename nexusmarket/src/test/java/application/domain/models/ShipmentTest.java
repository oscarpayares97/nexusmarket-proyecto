package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.ShipmentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipmentTest {

    private Shipment newShipment() {
        return new Shipment(null, 1L, "Street 1");
    }

    @Test
    void lifecycleReachesDelivered() {
        Shipment shipment = newShipment();
        shipment.pack();
        shipment.dispatch("FastCarrier", "TR-1");
        shipment.markInTransit();
        shipment.confirmDelivery();
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }

    @Test
    void cannotShipBeforePacking() {
        Shipment shipment = newShipment();
        assertThrows(InvalidStatusTransitionException.class, () -> shipment.dispatch("FastCarrier", "TR-1"));
    }

    @Test
    void shippingRequiresCarrierAndTrackingNumber() {
        Shipment shipment = newShipment();
        shipment.pack();
        assertThrows(InvalidDataException.class, () -> shipment.dispatch("", ""));
    }
}