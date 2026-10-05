package application.domain.services.returns;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.OrderItem;
import application.domain.models.Refund;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.in.RegisterInventoryReturnUseCase;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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

class ReturnFulfillmentServicesTest {

    private ReturnRepositoryPort returns;
    private RefundRepositoryPort refunds;
    private RegisterInventoryReturnUseCase inventoryReturn;
    private ReturnRequest request;
    private final ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();

    private final User ana = new User(1L, "1001", "Ana Admin", "ana@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        returns = mock(ReturnRepositoryPort.class);
        refunds = mock(RefundRepositoryPort.class);
        inventoryReturn = mock(RegisterInventoryReturnUseCase.class);

        OrderItem line = new OrderItem(1L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"), 3, 7L);
        request = new ReturnRequest(1L, 10L, 5L, List.of(ReturnItem.from(line, 1)), "Wrong size");

        when(returns.findById(1L)).thenReturn(Optional.of(request));
        when(returns.save(any(ReturnRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refunds.save(any(Refund.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void theAdministratorApprovesAReturn() {
        ReturnRequest approved = new ApproveReturnService(returns, authorization).approve(ana, 1L);
        assertEquals(ReturnStatus.APPROVED, approved.getStatus());
    }

    @Test
    void aBuyerCannotApproveAReturn() {
        ApproveReturnService service = new ApproveReturnService(returns, authorization);
        assertThrows(UnauthorizedOperationException.class, () -> service.approve(luis, 1L));
    }

    @Test
    void receivingAnApprovedReturnRestocksAndCreatesTheRefund() {
        request.approve();
        ReturnRequest received = new ReceiveReturnService(returns, refunds, inventoryReturn, authorization)
                .receive(oscar, 1L);
        assertEquals(ReturnStatus.RECEIVED, received.getStatus());
        verify(inventoryReturn).registerReturn(oscar, 1L, 7L, 1);
        verify(refunds).save(any(Refund.class));
    }

    @Test
    void cannotReceiveAReturnThatWasNotApproved() {
        ReceiveReturnService service = new ReceiveReturnService(returns, refunds, inventoryReturn, authorization);
        assertThrows(InvalidStatusTransitionException.class, () -> service.receive(oscar, 1L));
        verify(inventoryReturn, never()).registerReturn(any(User.class), anyLong(), anyLong(), anyInt());
    }

    @Test
    void aRefundCanOnlyBeProcessedOnce() {
        Refund refund = new Refund(1L, 1L, 10L, 5L, Money.of("35000"));
        when(refunds.findById(1L)).thenReturn(Optional.of(refund));
        ProcessRefundService service = new ProcessRefundService(refunds, authorization);

        assertEquals(RefundStatus.PROCESSED, service.process(ana, 1L).getStatus());
        assertThrows(InvalidStatusTransitionException.class, () -> service.process(ana, 1L));
    }
}