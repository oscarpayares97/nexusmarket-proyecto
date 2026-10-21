package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoadAuthenticatedUserServiceTest {

    private LoadAuthenticatedUserService service;
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        JwtServicePort jwt = mock(JwtServicePort.class);
        when(jwt.extractUserId("valid-token")).thenReturn(5L);
        when(jwt.extractUserId("ghost-token")).thenReturn(99L);
        when(users.findById(5L)).thenReturn(Optional.of(luis));
        service = new LoadAuthenticatedUserService(jwt, users);
    }

    @Test
    void loadsTheUserOfAValidToken() {
        assertEquals(luis, service.load("valid-token"));
    }

    @Test
    void rejectsATokenOfAnUnknownUser() {
        assertThrows(InvalidCredentialsException.class, () -> service.load("ghost-token"));
    }

    @Test
    void rejectsAnInactiveUser() {
        luis.block();
        assertThrows(UnauthorizedOperationException.class, () -> service.load("valid-token"));
    }
}