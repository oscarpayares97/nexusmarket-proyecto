package application.adapters.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private JwtProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtProvider("TEST-SECRET-TEST-SECRET-TEST-SECRET-1234", 3_600_000L);
    }

    private User user(Long id) {
        return new User(id, "1017123456", "Test User", "test@nexusmarket.com", "hash", UserRole.BUYER);
    }

    @Test
    void tokenCarriesUserClaims() {
        Claims claims = provider.getClaims(provider.generate(user(42L)));

        assertEquals("42", claims.getSubject());
        assertEquals("test@nexusmarket.com", claims.get("email"));
        assertEquals("BUYER", claims.get("role"));
        assertEquals("ACTIVE", claims.get("status"));
        assertNotNull(claims.getId());
    }

    @Test
    void extractUserIdRoundTrip() {
        assertEquals(42L, provider.extractUserId(provider.generate(user(42L))));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = provider.generate(user(42L));

        assertTrue(provider.validateToken(token));
        assertFalse(provider.validateToken(token + "x"));
        assertThrows(InvalidCredentialsException.class, () -> provider.extractUserId(token + "x"));
    }

    @Test
    void issuanceRequiresPersistedUser() {
        assertThrows(IllegalArgumentException.class, () -> provider.generate(user(null)));
        assertThrows(IllegalArgumentException.class, () -> provider.generate(null));
    }
}