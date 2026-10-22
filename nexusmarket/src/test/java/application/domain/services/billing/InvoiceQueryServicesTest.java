package application.domain.services.billing;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
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

class InvoiceQueryServicesTest {

    private ConsultInvoiceService consultOne;
    private ConsultInvoicesService consultAll;
    private Invoice invoice;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);
    private final User sara = new User(6L, "1004", "Sara Diaz", "sara@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        InvoiceRepositoryPort invoices = mock(InvoiceRepositoryPort.class);
        Product shirt = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        Order order = new Order(null, 5L);
        order.assignId(10L);
        order.addItem(shirt, 2);
        order.confirm("Street 1");
        order.markPaid();
        invoice = Invoice.issueFor(order, luis);

        when(invoices.findByOrderId(10L)).thenReturn(Optional.of(invoice));
        when(invoices.findAll()).thenReturn(List.of(invoice));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        consultOne = new ConsultInvoiceService(invoices, authorization, new ValidateOwnershipService());
        consultAll = new ConsultInvoicesService(invoices, authorization);
    }

    @Test
    void theOwnerConsultsItsInvoice() {
        assertEquals(invoice, consultOne.consultInvoice(luis, 10L));
    }

    @Test
    void anotherBuyerCannotConsultTheInvoice() {
        assertThrows(UnauthorizedOperationException.class, () -> consultOne.consultInvoice(sara, 10L));
    }

    @Test
    void onlyAdministratorsAndSupervisorsListAllTheInvoices() {
        assertEquals(1, consultAll.consultInvoices(admin).size());
        assertThrows(UnauthorizedOperationException.class, () -> consultAll.consultInvoices(luis));
    }
}