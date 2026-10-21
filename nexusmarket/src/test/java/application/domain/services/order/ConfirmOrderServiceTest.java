package application.domain.services.order;

import application.domain.services.inventory.ReserveInventoryService;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfirmOrderServiceTest {

    private ReserveInventoryService reserveInventory;
    private ConfirmOrderService service;
    private Buyer buyer;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);
        BuyerRepositoryPort buyers = mock(BuyerRepositoryPort.class);
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);
        reserveInventory = mock(ReserveInventoryService.class);

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        shirt.publish();
        buyer = new Buyer(5L, "Street 10 #5-20");
        Order cart = new Order(null, 5L);
        cart.addItem(shirt, 3);

        when(buyers.findByUserId(5L)).thenReturn(Optional.of(buyer));
        when(products.findById(1L)).thenReturn(Optional.of(shirt));
        when(orders.findOpenCartByBuyerId(5L)).thenReturn(Optional.of(cart));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reserveInventory.hasStock(1L, 3)).thenReturn(true);
        when(reserveInventory.reserve(luis, 1L, 3)).thenReturn(7L);

        service = new ConfirmOrderService(orders, buyers, products, reserveInventory,
                new ValidateRoleAuthorizationService());
    }

    @Test
    void confirmsTheOrderAndReservesTheStock() {
        Order order = service.confirm(luis, "Street 10 #5-20");
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(7L, order.getItems().get(0).getWarehouseId());
        verify(reserveInventory).reserve(luis, 1L, 3);
    }

    @Test
    void reservesNothingWhenThereIsNoStock() {
        when(reserveInventory.hasStock(1L, 3)).thenReturn(false);
        assertThrows(InsufficientStockException.class, () -> service.confirm(luis, "Street 10 #5-20"));
        verify(reserveInventory, never()).reserve(any(User.class), anyLong(), anyInt());
    }

    @Test
    void rejectsAnAddressThatIsNotRegistered() {
        assertThrows(InvalidDataException.class, () -> service.confirm(luis, "Made-up street 123"));
    }

    @Test
    void suspendedBuyersCannotPlaceOrders() {
        buyer.suspend();
        assertThrows(UnauthorizedOperationException.class, () -> service.confirm(luis, "Street 10 #5-20"));
    }
}