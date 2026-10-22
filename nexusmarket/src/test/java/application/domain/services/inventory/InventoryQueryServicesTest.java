package application.domain.services.inventory;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventoryQueryServicesTest {

    private ConsultInventoryService consultInventory;
    private ConsultInventoryMovementsService consultMovements;

    private final User pedro = new User(3L, "2002", "Pedro Mora", "pedro@mail.com", "hash", UserRole.SELLER);
    private final User oscar = new User(4L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        InventoryRepositoryPort inventories = mock(InventoryRepositoryPort.class);
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);
        Product shirt = new Product(5L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        when(products.findById(5L)).thenReturn(Optional.of(shirt));
        when(inventories.findByProductId(5L)).thenReturn(List.of(new Inventory(1L, 5L, 7L)));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        consultInventory = new ConsultInventoryService(inventories, products, authorization,
                new ValidateOwnershipService());
        consultMovements = new ConsultInventoryMovementsService(inventories, authorization);
    }

    @Test
    void theOperatorConsultsTheStockOfAProduct() {
        assertEquals(1, consultInventory.consultInventory(oscar, 5L).size());
    }

    @Test
    void aSellerCannotConsultAnotherSellersProduct() {
        assertThrows(UnauthorizedOperationException.class, () -> consultInventory.consultInventory(pedro, 5L));
    }

    @Test
    void buyersCannotConsultTheMovements() {
        assertThrows(UnauthorizedOperationException.class, () -> consultMovements.consultMovements(luis));
    }
}