package application.domain.models;

import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    private Inventory itemWith(int units) {
        Inventory item = new Inventory(null, 1L, 1L);
        item.registerEntry(units);
        return item;
    }

    @Test
    void reserveMovesUnitsFromAvailableToReserved() {
        Inventory item = itemWith(10);
        item.reserve(4);
        assertEquals(6, item.getAvailable());
        assertEquals(4, item.getReserved());
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        Inventory item = itemWith(5);
        assertThrows(InsufficientStockException.class, () -> item.reserve(6));
        assertEquals(5, item.getAvailable());   // no cambió nada
    }

    @Test
    void cannotReserveDamagedInventory() {
        Inventory item = itemWith(5);
        item.markDamaged();
        assertThrows(InvalidDataException.class, () -> item.reserve(1));
    }

    @Test
    void saleExitConsumesReservedUnits() {
        Inventory item = itemWith(10);
        item.reserve(3);
        item.registerSaleExit(3);
        assertEquals(0, item.getReserved());
        assertEquals(7, item.getAvailable());
    }

    @Test
    void cannotSellMoreThanReserved() {
        Inventory item = itemWith(10);
        item.reserve(2);
        assertThrows(InsufficientStockException.class, () -> item.registerSaleExit(3));
    }

    @Test
    void adjustmentCannotBeNegative() {
        Inventory item = itemWith(10);
        assertThrows(InvalidDataException.class, () -> item.adjustTo(-1));
        assertEquals(10, item.getAvailable());
    }
}