package application.domain.services.returns;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.in.RequestReturnUseCase.Command;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestReturnServiceTest {

    private OrderRepositoryPort orders;
    private ReturnRepositoryPort returns;
    private RequestReturnService service;
    private Order deliveredOrder;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User sara = new User(6L, "1004", "Sara Diaz", "sara@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        orders = mock(OrderRepositoryPort.class);
        returns = mock(ReturnRepositoryPort.class);

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        deliveredOrder = new Order(null, 5L);
        deliveredOrder.assignId(10L);
        deliveredOrder.addItem(shirt, 3);
        deliveredOrder.getItems().get(0).assignWarehouse(7L);
        deliveredOrder.confirm("Street 1");
        deliveredOrder.markPaid();
        deliveredOrder.markShipped();
        deliveredOrder.markDelivered();

        when(orders.findById(10L)).thenReturn(Optional.of(deliveredOrder));
        when(returns.save(any(ReturnRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new RequestReturnService(orders, returns, new ValidateRoleAuthorizationService(),
                new ValidateOwnershipService());
    }

    @Test
    void requestsTheReturnOfDeliveredUnits() {
        ReturnRequest request = service.request(luis, new Command(10L, Map.of(1L, 1), "Wrong size"));
        assertEquals(ReturnStatus.REQUESTED, request.getStatus());
        assertEquals(Money.of("35000"), request.total());
    }

    @Test
    void cannotReturnMoreUnitsThanPurchased() {
        assertThrows(InvalidDataException.class,
                () -> service.request(luis, new Command(10L, Map.of(1L, 4), "Wrong size")));
    }

    @Test
    void unitsAlreadyInProcessAreDiscounted() {
        ReturnRequest existing = new ReturnRequest(1L, 10L, 5L,
                List.of(ReturnItem.from(deliveredOrder.getItems().get(0), 2)), "Wrong size");
        when(returns.findByOrderId(10L)).thenReturn(List.of(existing));
        assertThrows(InvalidDataException.class,
                () -> service.request(luis, new Command(10L, Map.of(1L, 2), "Another reason")));
    }

    @Test
    void onlyDeliveredOrdersCanHaveReturns() {
        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Order shipped = new Order(null, 5L);
        shipped.assignId(11L);
        shipped.addItem(shirt, 1);
        shipped.confirm("Street 1");
        shipped.markPaid();
        shipped.markShipped();
        when(orders.findById(11L)).thenReturn(Optional.of(shipped));
        assertThrows(InvalidStatusTransitionException.class,
                () -> service.request(luis, new Command(11L, Map.of(1L, 1), "Wrong size")));
    }

    @Test
    void aBuyerCannotReturnAnotherBuyersOrder() {
        assertThrows(UnauthorizedOperationException.class,
                () -> service.request(sara, new Command(10L, Map.of(1L, 1), "Wrong size")));
    }
}