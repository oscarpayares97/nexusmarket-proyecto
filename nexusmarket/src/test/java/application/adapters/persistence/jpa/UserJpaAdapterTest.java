package application.adapters.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserJpaAdapterTest {

    @Autowired
    private UserRepositoryPort users;

    @Test
    void saveAssignsIdAndAllowsLookups() {
        User saved = users.save(new User(null, "TEST-001", "Persist Test",
                "persist.test@nexusmarket.com", "hash", UserRole.BUYER));

        assertNotNull(saved.getId());
        assertTrue(users.findByEmail("PERSIST.TEST@nexusmarket.com").isPresent());
        assertTrue(users.existsByDocument("TEST-001"));
    }

    @Test
    void unknownEmailIsNotFound() {
        assertFalse(users.findByEmail("nobody@nexusmarket.com").isPresent());
        assertFalse(users.existsByEmail("nobody@nexusmarket.com"));
    }
}