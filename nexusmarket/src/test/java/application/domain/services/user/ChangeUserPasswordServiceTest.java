package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangeUserPasswordServiceTest {

    private UserRepositoryPort users;
    private ChangeUserPasswordService service;
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "old-hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        users = mock(UserRepositoryPort.class);
        PasswordServicePort passwords = mock(PasswordServicePort.class);
        when(users.findById(5L)).thenReturn(Optional.of(luis));
        when(passwords.matches("oldSecret1", "old-hash")).thenReturn(true);
        when(passwords.encode("newSecret1")).thenReturn("new-hash");
        service = new ChangeUserPasswordService(users, passwords, new ValidateRoleAuthorizationService());
    }

    @Test
    void changesThePasswordHash() {
        service.change(luis, "oldSecret1", "newSecret1");
        assertEquals("new-hash", luis.getPasswordHash());
        verify(users).save(luis);
    }

    @Test
    void rejectsAWrongCurrentPassword() {
        assertThrows(InvalidCredentialsException.class, () -> service.change(luis, "wrong", "newSecret1"));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void rejectsAShortNewPassword() {
        assertThrows(InvalidDataException.class, () -> service.change(luis, "oldSecret1", "123"));
    }
}