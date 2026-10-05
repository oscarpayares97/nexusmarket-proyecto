package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthorizationServicesTest {

    private final ValidateRoleAuthorizationService roles = new ValidateRoleAuthorizationService();
    private final ValidateOwnershipService ownership = new ValidateOwnershipService();

    private User userWith(UserRole role) {
        return new User(1L, "1001", "Ana Gomez", "ana@mail.com", "hash", role);
    }

    @Test
    void allowsAnActiveUserWithAnAllowedRole() {
        assertDoesNotThrow(() -> roles.requireRole(userWith(UserRole.SELLER), UserRole.SELLER, UserRole.ADMINISTRATOR));
    }

    @Test
    void rejectsARoleThatIsNotAllowed() {
        assertThrows(UnauthorizedOperationException.class,
                () -> roles.requireRole(userWith(UserRole.BUYER), UserRole.ADMINISTRATOR));
    }

    @Test
    void rejectsAnUnauthenticatedUser() {
        assertThrows(UnauthorizedOperationException.class, () -> roles.requireRole(null, UserRole.BUYER));
    }

    @Test
    void rejectsABlockedUser() {
        User blocked = userWith(UserRole.BUYER);
        blocked.block();
        assertThrows(UnauthorizedOperationException.class, () -> roles.requireRole(blocked, UserRole.BUYER));
    }

    @Test
    void ownershipRequiresTheSameUser() {
        User owner = userWith(UserRole.BUYER);
        assertDoesNotThrow(() -> ownership.requireOwner(owner, 1L));
        assertThrows(UnauthorizedOperationException.class, () -> ownership.requireOwner(owner, 2L));
    }
}