package application.domain.services.user;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.in.AdministratorPort.RegisterInternalUserCommand;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterInternalUserServiceTest {

    private UserRepositoryPort users;
    private RegisterInternalUserService service;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User sofia = new User(4L, "4001", "Sofia Rojas", "sofia@mail.com", "hash", UserRole.SUPERVISOR);

    @BeforeEach
    void setUp() {
        users = mock(UserRepositoryPort.class);
        PasswordServicePort passwords = mock(PasswordServicePort.class);
        when(passwords.encode("secret123")).thenReturn("hash");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new RegisterInternalUserService(users, passwords, new ValidateRoleAuthorizationService());
    }

    private RegisterInternalUserCommand command(UserRole role) {
        return new RegisterInternalUserCommand("3001", "Oscar Vega", "oscar@mail.com", "secret123", role);
    }

    @Test
    void registersALogisticsOperator() {
        User created = service.register(admin, command(UserRole.LOGISTICS_OPERATOR));
        assertEquals(UserRole.LOGISTICS_OPERATOR, created.getRole());
        assertEquals("hash", created.getPasswordHash());
    }

    @Test
    void rejectsTheBuyerRole() {
        assertThrows(InvalidDataException.class, () -> service.register(admin, command(UserRole.BUYER)));
    }

    @Test
    void rejectsADuplicatedEmail() {
        when(users.existsByEmail("oscar@mail.com")).thenReturn(true);
        assertThrows(DuplicateEntityException.class,
                () -> service.register(admin, command(UserRole.LOGISTICS_OPERATOR)));
    }

    @Test
    void onlyAdministratorsRegisterInternalUsers() {
        assertThrows(UnauthorizedOperationException.class,
                () -> service.register(sofia, command(UserRole.LOGISTICS_OPERATOR)));
    }
}