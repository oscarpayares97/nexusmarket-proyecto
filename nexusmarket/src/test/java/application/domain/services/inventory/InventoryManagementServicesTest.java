package application.domain.services.inventory;

import application.domain.exceptions.InvalidDataException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryManagementServicesTest {

    private InventoryRepositoryPort inventories;
    private Inventory stock;
    private AdjustInventoryService adjust;
    private ChangeInventoryStatusService changeStatus;

    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);

    @BeforeEach
    void setUp() {
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);
        WarehouseRepositoryPort warehouses = mock(WarehouseRepositoryPort.class);
        inventories = mock(InventoryRepositoryPort.class);

        Product shirt = new Product(5L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Warehouse warehouse = new Warehouse(7L, "Main", "Zone 1", WarehouseType.SELLER, 2L);
        stock = new Inventory(1L, 5L, 7L);
        stock.registerEntry(10);

        when(products.findById(5L)).thenReturn(Optional.of(shirt));
        when(warehouses.findById(7L)).thenReturn(Optional.of(warehouse));
        when(inventories.findByProductAndWarehouse(5L, 7L)).thenReturn(Optional.of(stock));
        when(inventories.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryOperationGuard guard = new InventoryOperationGuard(products, warehouses, inventories,
                new ValidateRoleAuthorizationService());
        adjust = new AdjustInventoryService(inventories, guard);
        changeStatus = new ChangeInventoryStatusService(inventories, guard);
    }

    @Test
    void adjustSetsTheCountedQuantityAndRecordsTheMovement() {
        assertEquals(8, adjust.adjust(marta, 5L, 7L, 8).getAvailable());
        verify(inventories).saveMovement(any(InventoryMovement.class));
    }

    @Test
    void aNegativeAdjustmentIsRejected() {
        assertThrows(InvalidDataException.class, () -> adjust.adjust(marta, 5L, 7L, -1));
        assertEquals(10, stock.getAvailable());
    }

    @Test
    void markingAsDamagedChangesTheStatus() {
        assertEquals(InventoryStatus.DAMAGED,
                changeStatus.change(marta, 5L, 7L, InventoryStatus.DAMAGED).getStatus());
    }
}