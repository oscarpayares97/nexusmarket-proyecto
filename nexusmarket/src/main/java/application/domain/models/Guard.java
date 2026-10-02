package application.domain.models;

import application.domain.exceptions.InvalidDataException;

public final class Guard {

    private Guard() {
    }

    // Exige texto no vacío y lo devuelve sin espacios sobrantes
    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field + " Es requerido.");
        }
        return value.trim();
    }

    public static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new InvalidDataException(field + " Es requerido.");
        }
    }

    public static void requirePositive(int value, String field) {
        if (value <= 0) {
            throw new InvalidDataException(field + " Debe ser mayor que cero.");
        }
    }
}