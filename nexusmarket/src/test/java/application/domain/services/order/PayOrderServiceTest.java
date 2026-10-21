package application.domain.services.order;

import application.domain.services.billing.IssueInvoiceService;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PayOrderServiceTest {

    private IssueInvoiceService issueInvoice;
    private PayOrderService service;
    private Order physicalOrder;
    private Order digitalOrder;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User sara = new User(6L, "1004", "Sara Diaz", "sara@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);
        issueInvoice = mock(IssueInvoiceService.class);

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Product ebook = new Product(2L, 2L, "Ebook", ProductType.DIGITAL, Money.of("50000"));

        physicalOrder = new Order(null, 5L);
        physicalOrder.assignId(10L);
        physicalOrder.addItem(shirt, 1);
        physicalOrder.confirm("Street 1");

        digitalOrder = new Order(null, 5L);
        digitalOrder.assignId(11L);
        digitalOrder.addItem(ebook, 1);
        digitalOrder.confirm("Street 1");

        when(orders.findById(10L)).thenReturn(Optional.of(physicalOrder));
        when(orders.findById(11L)).thenReturn(Optional.of(digitalOrder));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new PayOrderService(orders, issueInvoice, new ValidateRoleAuthorizationService(),
                new ValidateOwnershipService());
    }

    @Test
    void approvedPaymentMarksTheOrderPaidAndIssuesTheInvoice() {
        Order order = service.pay(luis, 10L, true);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(issueInvoice).issue(physicalOrder, luis);
    }

    @Test
    void digitalOrdersAreDeliveredRightAfterThePayment() {
        Order order = service.pay(luis, 11L, true);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void aRejectedPaymentKeepsTheOrderPending() {
        assertThrows(InvalidDataException.class, () -> service.pay(luis, 10L, false));
        assertEquals(OrderStatus.PENDING_PAYMENT, physicalOrder.getStatus());
        verify(issueInvoice, never()).issue(any(Order.class), any(User.class));
    }

    @Test
    void aBuyerCannotPayAnotherBuyersOrder() {
        assertThrows(UnauthorizedOperationException.class, () -> service.pay(sara, 10L, true));
    }
}