package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort.CreateWarehouseCommand;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateWarehouseServiceTest {

    private CreateWarehouseService service;
    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);

    @BeforeEach
    void setUp() {
        WarehouseRepositoryPort warehouses = mock(WarehouseRepositoryPort.class);
        SellerRepositoryPort sellers = mock(SellerRepositoryPort.class);
        when(warehouses.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new CreateWarehouseService(warehouses, sellers, new ValidateRoleAuthorizationService());
    }

    @Test
    void withoutSellerTheWarehouseBelongsToTheMarketplace() {
        Warehouse warehouse = service.create(admin, new CreateWarehouseCommand("Central", "Zone 5", null));
        assertEquals(WarehouseType.MARKETPLACE, warehouse.getType());
        assertNull(warehouse.getSellerId());
    }

    @Test
    void aSellerWarehouseRequiresAnExistingSeller() {
        assertThrows(EntityNotFoundException.class,
                () -> service.create(admin, new CreateWarehouseCommand("Branch", "Zone 2", 99L)));
    }
}