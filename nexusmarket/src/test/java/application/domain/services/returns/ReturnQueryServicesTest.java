package application.domain.services.returns;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.OrderItem;
import application.domain.models.Refund;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReturnQueryServicesTest {

    private ReturnRepositoryPort returns;
    private ConsultMyReturnsService consultMine;
    private ConsultReturnsService consultAll;
    private ConsultRefundsService consultRefunds;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        returns = mock(ReturnRepositoryPort.class);
        RefundRepositoryPort refunds = mock(RefundRepositoryPort.class);

        OrderItem line = new OrderItem(1L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"), 3, 7L);
        ReturnRequest request = new ReturnRequest(1L, 10L, 5L, List.of(ReturnItem.from(line, 1)), "Wrong size");
        when(returns.findByBuyerId(5L)).thenReturn(List.of(request));
        when(refunds.findAll()).thenReturn(List.of(new Refund(1L, 1L, 10L, 5L, Money.of("35000"))));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        consultMine = new ConsultMyReturnsService(returns, authorization);
        consultAll = new ConsultReturnsService(returns, authorization);
        consultRefunds = new ConsultRefundsService(refunds, authorization);
    }

    @Test
    void aBuyerSeesOnlyItsOwnReturns() {
        assertEquals(1, consultMine.consultMyReturns(luis).size());
        verify(returns, never()).findAll();
    }

    @Test
    void onlyAdministratorsAndSupervisorsSeeTheRefunds() {
        assertEquals(1, consultRefunds.consultRefunds(admin).size());
        assertThrows(UnauthorizedOperationException.class, () -> consultRefunds.consultRefunds(oscar));
    }

    @Test
    void buyersCannotConsultAllTheReturns() {
        assertThrows(UnauthorizedOperationException.class, () -> consultAll.consultReturns(luis));
    }
}  