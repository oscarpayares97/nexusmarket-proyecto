package application.domain.services.catalog;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.SellerPort.RegisterProductCommand;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogServicesTest {

    private ProductRepositoryPort products;
    private RegisterProductService register;
    private PublishProductService publish;
    private ConsultPublishedProductsService consult;
    private Product shirt;

    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User pedro = new User(3L, "2002", "Pedro Mora", "pedro@mail.com", "hash", UserRole.SELLER);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        products = mock(ProductRepositoryPort.class);
        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        register = new RegisterProductService(products, authorization);
        publish = new PublishProductService(products, authorization, new ValidateOwnershipService());
        consult = new ConsultPublishedProductsService(products);

        shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        when(products.findById(1L)).thenReturn(Optional.of(shirt));
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registeringAssignsTheActorAsTheSeller() {
        Product product = register.register(marta,
                new RegisterProductCommand("Hat", ProductType.PHYSICAL, new BigDecimal("20000")));
        assertEquals(2L, product.getSellerId());
        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
    }

    @Test
    void aBuyerCannotRegisterProducts() {
        assertThrows(UnauthorizedOperationException.class, () -> register.register(luis,
                new RegisterProductCommand("Hat", ProductType.PHYSICAL, new BigDecimal("20000"))));
    }

    @Test
    void onlyTheOwnerCanPublish() {
        assertThrows(UnauthorizedOperationException.class, () -> publish.publish(pedro, 1L));
        verify(products, never()).save(any(Product.class));
    }

    @Test
    void theOwnerPublishesItsProduct() {
        Product published = publish.publish(marta, 1L);
        assertEquals(ProductStatus.PUBLISHED, published.getStatus());
    }

    @Test
    void consultReturnsThePublishedProducts() {
        when(products.findPublished()).thenReturn(List.of(shirt));
        assertEquals(1, consult.consult().size());
    }
}