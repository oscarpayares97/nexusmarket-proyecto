package application.domain.services.inventory;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterInventoryEntryServiceTest {

    private InventoryRepositoryPort inventories;
    private RegisterInventoryEntryService service;

    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User pedro = new User(3L, "2002", "Pedro Mora", "pedro@mail.com", "hash", UserRole.SELLER);

    @BeforeEach
    void setUp() {
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);
        WarehouseRepositoryPort warehouses = mock(WarehouseRepositoryPort.class);
        inventories = mock(InventoryRepositoryPort.class);

        Product shirt = new Product(5L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Product ebook = new Product(6L, 2L, "Ebook", ProductType.DIGITAL, Money.of("50000"));
        Warehouse warehouse = new Warehouse(7L, "Main warehouse", "Zone 1", WarehouseType.SELLER, 2L);
        when(products.findById(5L)).thenReturn(Optional.of(shirt));
        when(products.findById(6L)).thenReturn(Optional.of(ebook));
        when(warehouses.findById(7L)).thenReturn(Optional.of(warehouse));
        when(inventories.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryOperationGuard guard = new InventoryOperationGuard(products, warehouses, inventories,
                new ValidateRoleAuthorizationService());
        service = new RegisterInventoryEntryService(inventories, guard);
    }

    @Test
    void aSellerRegistersAnEntryInItsOwnWarehouse() {
        Inventory inventory = service.register(marta, 5L, 7L, 20);
        assertEquals(20, inventory.getAvailable());
        verify(inventories).saveMovement(any(InventoryMovement.class));
    }

    @Test
    void anotherSellerCannotOperateTheInventory() {
        assertThrows(UnauthorizedOperationException.class, () -> service.register(pedro, 5L, 7L, 20));
        verify(inventories, never()).save(any(Inventory.class));
    }

    @Test
    void digitalProductsDoNotManageInventory() {
        assertThrows(InvalidDataException.class, () -> service.register(marta, 6L, 7L, 5));
    }
}