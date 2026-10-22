package application.domain.services.warehouse;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarehouseManagementServicesTest {

    private WarehouseRepositoryPort warehouses;
    private ChangeWarehouseStatusService changeStatus;
    private ConsultWarehousesService consult;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);

    @BeforeEach
    void setUp() {
        warehouses = mock(WarehouseRepositoryPort.class);
        Warehouse martaWarehouse = new Warehouse(7L, "Main", "Zone 1", WarehouseType.SELLER, 2L);
        Warehouse central = new Warehouse(8L, "Central", "Zone 5", WarehouseType.MARKETPLACE, null);

        when(warehouses.findById(7L)).thenReturn(Optional.of(martaWarehouse));
        when(warehouses.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(warehouses.findAll()).thenReturn(List.of(martaWarehouse, central));
        when(warehouses.findBySellerId(2L)).thenReturn(List.of(martaWarehouse));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        changeStatus = new ChangeWarehouseStatusService(warehouses, authorization);
        consult = new ConsultWarehousesService(warehouses, authorization);
    }

    @Test
    void theAdministratorDeactivatesAWarehouse() {
        assertFalse(changeStatus.change(admin, 7L, false).isActive());
    }

    @Test
    void theOperatorSeesAllTheWarehouses() {
        assertEquals(2, consult.consultWarehouses(oscar).size());
    }

    @Test
    void aSellerSeesOnlyItsOwnWarehouses() {
        assertEquals(1, consult.consultWarehouses(marta).size());
        verify(warehouses, never()).findAll();
    }
}