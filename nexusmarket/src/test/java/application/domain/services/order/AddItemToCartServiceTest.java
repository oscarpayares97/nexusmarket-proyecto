package application.domain.services.order;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddItemToCartServiceTest {

    private ProductRepositoryPort products;
    private AddItemToCartService service;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);

    @BeforeEach
    void setUp() {
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);
        products = mock(ProductRepositoryPort.class);

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        shirt.publish();
        when(products.findById(1L)).thenReturn(Optional.of(shirt));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new AddItemToCartService(orders, products, new ValidateRoleAuthorizationService());
    }

    @Test
    void createsTheCartAndAddsTheItem() {
        Order cart = service.add(luis, 1L, 2);
        assertEquals(OrderStatus.CART, cart.getStatus());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void rejectsAProductThatIsNotPublished() {
        Product draft = new Product(2L, 2L, "Draft", ProductType.PHYSICAL, Money.of("10000"));
        when(products.findById(2L)).thenReturn(Optional.of(draft));
        assertThrows(InvalidDataException.class, () -> service.add(luis, 2L, 1));
    }

    @Test
    void onlyBuyersHaveACart() {
        assertThrows(UnauthorizedOperationException.class, () -> service.add(marta, 1L, 1));
    }
}