package application.domain.valueobjects;

import application.domain.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentNumberTest {

    @Test
    void trimsTheValue() {
        assertEquals("1017123456", new DocumentNumber(" 1017123456 ").value());
    }

    @Test
    void rejectsInvalidCharactersOrTooLongValues() {
        assertThrows(InvalidDataException.class, () -> new DocumentNumber("12 34"));
        assertThrows(InvalidDataException.class, () -> new DocumentNumber("1".repeat(21)));
    }
}