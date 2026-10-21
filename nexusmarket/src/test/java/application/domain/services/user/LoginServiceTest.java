package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    private LoginService service;
    private User ana;

    @BeforeEach
    void setUp() {
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        PasswordServicePort passwords = mock(PasswordServicePort.class);
        JwtServicePort jwt = mock(JwtServicePort.class);

        ana = new User(1L, "1001", "Ana Gomez", "ana@mail.com", "hash", UserRole.BUYER);
        when(users.findByEmail("ana@mail.com")).thenReturn(Optional.of(ana));
        when(passwords.matches("secret123", "hash")).thenReturn(true);
        when(jwt.generate(ana)).thenReturn("token-123");

        service = new LoginService(users, passwords, jwt);
    }

    @Test
    void returnsATokenForValidCredentials() {
        AuthenticationResult result = service.login("ana@mail.com", "secret123");
        assertEquals("token-123", result.token());
        assertEquals(ana, result.user());
    }

    @Test
    void rejectsAnUnknownEmail() {
        assertThrows(InvalidCredentialsException.class, () -> service.login("nobody@mail.com", "secret123"));
    }

    @Test
    void rejectsAWrongPassword() {
        assertThrows(InvalidCredentialsException.class, () -> service.login("ana@mail.com", "wrong"));
    }

    @Test
    void rejectsABlockedUser() {
        ana.block();
        assertThrows(UnauthorizedOperationException.class, () -> service.login("ana@mail.com", "secret123"));
    }
}