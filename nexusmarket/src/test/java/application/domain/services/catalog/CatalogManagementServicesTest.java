package application.domain.services.catalog;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogManagementServicesTest {

    private Product shirt;
    private AddProductVariantService addVariant;
    private SuspendProductService suspend;
    private DiscontinueProductService discontinue;

    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User pedro = new User(3L, "2002", "Pedro Mora", "pedro@mail.com", "hash", UserRole.SELLER);

    @BeforeEach
    void setUp() {
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);
        shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        when(products.findById(1L)).thenReturn(Optional.of(shirt));
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OwnedProductLoader loader = new OwnedProductLoader(products,
                new ValidateRoleAuthorizationService(), new ValidateOwnershipService());
        addVariant = new AddProductVariantService(loader, products);
        suspend = new SuspendProductService(loader, products);
        discontinue = new DiscontinueProductService(loader, products);
    }

    @Test
    void theOwnerAddsAVariant() {
        addVariant.add(marta, 1L, "Black - L");
        assertTrue(shirt.getVariants().contains("Black - L"));
    }

    @Test
    void anotherSellerCannotSuspendTheProduct() {
        assertThrows(UnauthorizedOperationException.class, () -> suspend.suspend(pedro, 1L));
    }

    @Test
    void theOwnerSuspendsAPublishedProduct() {
        shirt.publish();
        assertEquals(ProductStatus.SUSPENDED, suspend.suspend(marta, 1L).getStatus());
    }

    @Test
    void aDiscontinuedProductCannotBeSuspended() {
        discontinue.discontinue(marta, 1L);
        assertThrows(InvalidStatusTransitionException.class, () -> suspend.suspend(marta, 1L));
    }
}