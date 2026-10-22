package application.domain.services.report;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.SalesReport;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
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

class GenerateSalesReportServiceTest {

    private GenerateSalesReportService service;

    private final User sofia = new User(4L, "4001", "Sofia Rojas", "sofia@mail.com", "hash", UserRole.SUPERVISOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        InvoiceRepositoryPort invoices = mock(InvoiceRepositoryPort.class);
        RefundRepositoryPort refunds = mock(RefundRepositoryPort.class);
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);
        ProductRepositoryPort products = mock(ProductRepositoryPort.class);

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Product ebook = new Product(2L, 3L, "Ebook", ProductType.DIGITAL, Money.of("50000"));
        when(products.findById(1L)).thenReturn(Optional.of(shirt));
        when(products.findById(2L)).thenReturn(Optional.of(ebook));

        Order order = new Order(null, 5L);
        order.assignId(10L);
        order.addItem(shirt, 3);
        order.addItem(ebook, 1);
        order.confirm("Street 1");
        order.markPaid();
        Invoice invoice = Invoice.issueFor(order, luis);

        Refund refund = new Refund(1L, 1L, 10L, 5L, Money.of("35000"));
        refund.process();

        when(invoices.findAll()).thenReturn(List.of(invoice));
        when(refunds.findAll()).thenReturn(List.of(refund));
        when(orders.findAll()).thenReturn(List.of(order));

        service = new GenerateSalesReportService(invoices, refunds, orders, products,
                new ValidateRoleAuthorizationService());
    }

    @Test
    void computesGrossRefundedNetSalesAndOrdersByStatus() {
        SalesReport report = service.generate(sofia);
        assertEquals(Money.of("155000"), report.grossSales());
        assertEquals(Money.of("35000"), report.refunded());
        assertEquals(Money.of("120000"), report.netSales());
        assertEquals(1, report.ordersByStatus().get(OrderStatus.PAID));
        assertEquals(0, report.ordersByStatus().get(OrderStatus.CART));
    }

    @Test
    void groupsTheSalesBySeller() {
        SalesReport report = service.generate(sofia);
        assertEquals(Money.of("105000"), report.salesBySeller().get(2L));
        assertEquals(Money.of("50000"), report.salesBySeller().get(3L));
    }

    @Test
    void buyersCannotGenerateReports() {
        assertThrows(UnauthorizedOperationException.class, () -> service.generate(luis));
    }
}