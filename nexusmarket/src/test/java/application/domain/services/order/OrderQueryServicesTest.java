package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderQueryServicesTest {

    private OrderRepositoryPort orders;
    private RemoveItemFromCartService remove;
    private ConsultMyOrdersService consultMine;
    private ConsultOrdersService consultAll;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User sara = new User(6L, "1004", "Sara Diaz", "sara@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        orders = mock(OrderRepositoryPort.class);
        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Product ebook = new Product(2L, 2L, "Ebook", ProductType.DIGITAL, Money.of("50000"));
        Order cart = new Order(null, 5L);
        cart.addItem(shirt, 3);
        cart.addItem(ebook, 1);

        when(orders.findOpenCartByBuyerId(5L)).thenReturn(Optional.of(cart));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orders.findByBuyerId(5L)).thenReturn(List.of(cart));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        remove = new RemoveItemFromCartService(orders, authorization);
        consultMine = new ConsultMyOrdersService(orders, authorization);
        consultAll = new ConsultOrdersService(orders, authorization);
    }

    @Test
    void removesAnItemFromTheCart() {
        assertEquals(1, remove.remove(luis, 1L).getItems().size());
    }

    @Test
    void cannotRemoveFromAMissingCart() {
        assertThrows(EntityNotFoundException.class, () -> remove.remove(sara, 1L));
    }

    @Test
    void aBuyerSeesOnlyItsOwnOrders() {
        assertEquals(1, consultMine.consultMyOrders(luis).size());
        verify(orders, never()).findAll();
    }

    @Test
    void aBuyerCannotConsultAllTheOrders() {
        assertThrows(UnauthorizedOperationException.class, () -> consultAll.consultOrders(luis));
    }
}