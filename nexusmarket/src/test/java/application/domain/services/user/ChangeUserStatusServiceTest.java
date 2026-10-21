package application.domain.services.user;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangeUserStatusServiceTest {

    private ChangeUserStatusService service;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User sofia = new User(4L, "4001", "Sofia Rojas", "sofia@mail.com", "hash", UserRole.SUPERVISOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        when(users.findById(5L)).thenReturn(Optional.of(luis));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new ChangeUserStatusService(users, new ValidateRoleAuthorizationService());
    }

    @Test
    void blocksAUser() {
        assertEquals(UserStatus.BLOCKED, service.change(admin, 5L, UserStatus.BLOCKED).getStatus());
    }

    @Test
    void anAdministratorCannotChangeItsOwnStatus() {
        assertThrows(InvalidDataException.class, () -> service.change(admin, 1L, UserStatus.BLOCKED));
    }

    @Test
    void onlyAdministratorsChangeTheStatus() {
        assertThrows(UnauthorizedOperationException.class, () -> service.change(sofia, 5L, UserStatus.BLOCKED));
    }

    @Test
    void reactivatesABlockedUser() {
        luis.block();
        assertEquals(UserStatus.ACTIVE, service.change(admin, 5L, UserStatus.ACTIVE).getStatus());
    }
}