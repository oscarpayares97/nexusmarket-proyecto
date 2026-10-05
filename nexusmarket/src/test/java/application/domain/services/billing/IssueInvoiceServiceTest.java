package application.domain.services.billing;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.valueobjects.Money;
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

class IssueInvoiceServiceTest {

    private InvoiceRepositoryPort invoices;
    private IssueInvoiceService service;
    private Order paidOrder;

    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        invoices = mock(InvoiceRepositoryPort.class);
        when(invoices.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice saved = invocation.getArgument(0);
            saved.assignId(1L);
            return saved;
        });

        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        paidOrder = new Order(null, 5L);
        paidOrder.assignId(10L);
        paidOrder.addItem(shirt, 2);
        paidOrder.confirm("Street 1");
        paidOrder.markPaid();

        service = new IssueInvoiceService(invoices);
    }

    @Test
    void issuesAnInvoiceWithTheBuyerDataAndTheTotal() {
        Invoice invoice = service.issue(paidOrder, luis);
        assertEquals("INV-000001", invoice.getNumber());
        assertEquals(Money.of("70000"), invoice.getTotal());
        assertEquals("Luis Perez", invoice.getBuyerName());
    }

    @Test
    void anOrderHasOnlyOneInvoice() {
        when(invoices.findByOrderId(10L)).thenReturn(Optional.of(Invoice.issueFor(paidOrder, luis)));
        assertThrows(DuplicateEntityException.class, () -> service.issue(paidOrder, luis));
    }
}