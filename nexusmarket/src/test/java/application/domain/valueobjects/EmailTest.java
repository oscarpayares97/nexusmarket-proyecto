package application.domain.valueobjects;

import application.domain.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void normalizesToLowercaseWithoutSpaces() {
        assertEquals("ana@mail.com", new Email("  Ana@Mail.COM ").value());
    }

    @Test
    void rejectsAnInvalidFormat() {
        assertThrows(InvalidDataException.class, () -> new Email("not-an-email"));
    }

    @Test
    void rejectsABlankEmail() {
        assertThrows(InvalidDataException.class, () -> new Email("  "));
    }
}