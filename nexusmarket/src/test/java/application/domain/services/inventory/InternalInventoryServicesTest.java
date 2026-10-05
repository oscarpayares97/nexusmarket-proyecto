package application.domain.services.inventory;

import application.domain.exceptions.InsufficientStockException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternalInventoryServicesTest {

    private InventoryRepositoryPort inventories;
    private Inventory inventory;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);

    @BeforeEach
    void setUp() {
        inventories = mock(InventoryRepositoryPort.class);
        inventory = new Inventory(1L, 1L, 7L);
        inventory.registerEntry(10);
        when(inventories.findByProductId(1L)).thenReturn(List.of(inventory));
        when(inventories.findByProductAndWarehouse(1L, 7L)).thenReturn(Optional.of(inventory));
    }

    @Test
    void reservesInTheWarehouseThatHasStock() {
        Long warehouseId = new ReserveInventoryService(inventories).reserve(luis, 1L, 4);
        assertEquals(7L, warehouseId);
        assertEquals(6, inventory.getAvailable());
        assertEquals(4, inventory.getReserved());
        verify(inventories).saveMovement(any(InventoryMovement.class));
    }

    @Test
    void hasStockComparesWithTheAvailableUnits() {
        ReserveInventoryService service = new ReserveInventoryService(inventories);
        assertTrue(service.hasStock(1L, 10));
        assertFalse(service.hasStock(1L, 11));
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        ReserveInventoryService service = new ReserveInventoryService(inventories);
        assertThrows(InsufficientStockException.class, () -> service.reserve(luis, 1L, 11));
        verify(inventories, never()).save(any(Inventory.class));
    }

    @Test
    void aSaleConsumesTheReservedUnits() {
        inventory.reserve(4);
        new RegisterInventorySaleService(inventories).registerSale(oscar, 1L, 7L, 4);
        assertEquals(0, inventory.getReserved());
        verify(inventories).saveMovement(any(InventoryMovement.class));
    }

    @Test
    void aReturnPutsTheUnitsBackAsAvailable() {
        new RegisterInventoryReturnService(inventories).registerReturn(oscar, 1L, 7L, 2);
        assertEquals(12, inventory.getAvailable());
    }
}