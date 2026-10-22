package application.domain.services.returns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.OrderItem;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RejectReturnServiceTest {

    private ReturnRepositoryPort returns;
    private RejectReturnService service;
    private ReturnRequest request;

    private final User ana = new User(1L, "1001", "Ana Admin", "ana@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        returns = mock(ReturnRepositoryPort.class);
        OrderItem line = new OrderItem(1L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"), 3, 7L);
        request = new ReturnRequest(1L, 10L, 5L, List.of(ReturnItem.from(line, 1)), "Wrong size");
        when(returns.findById(1L)).thenReturn(Optional.of(request));
        when(returns.save(any(ReturnRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new RejectReturnService(returns, new ValidateRoleAuthorizationService());
    }

    @Test
    void theAdministratorRejectsAReturnWithAReason() {
        ReturnRequest rejected = service.reject(ana, 1L, "Outside the return period");

        assertEquals("Outside the return period", rejected.getRejectionReason());
        assertNotEquals(ReturnStatus.APPROVED, rejected.getStatus());
        verify(returns).save(request);
    }

    @Test
    void aBuyerCannotRejectAReturn() {
        assertThrows(UnauthorizedOperationException.class, () -> service.reject(luis, 1L, "No"));
        verify(returns, never()).save(any(ReturnRequest.class));
    }

    @Test
    void rejectingAnUnknownReturnFails() {
        when(returns.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.reject(ana, 99L, "Reason"));
    }
}