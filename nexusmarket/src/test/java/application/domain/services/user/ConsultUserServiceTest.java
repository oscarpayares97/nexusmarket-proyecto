package application.domain.services.user;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConsultUserServiceTest {

    private ConsultUserService service;

    private final User sofia = new User(4L, "4001", "Sofia Rojas", "sofia@mail.com", "hash", UserRole.SUPERVISOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        when(users.findAll()).thenReturn(List.of(luis));
        service = new ConsultUserService(users, new ValidateRoleAuthorizationService());
    }

    @Test
    void aSupervisorConsultsTheUsers() {
        assertEquals(1, service.consultUsers(sofia).size());
    }

    @Test
    void aBuyerCannotConsultTheUsers() {
        assertThrows(UnauthorizedOperationException.class, () -> service.consultUsers(luis));
    }
}