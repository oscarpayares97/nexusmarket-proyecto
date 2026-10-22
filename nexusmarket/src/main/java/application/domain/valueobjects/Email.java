package application.domain.valueobjects;

import application.domain.exceptions.InvalidDataException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MAX_LENGTH = 100;

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException("El email es requerido.");
        }
        value = value.trim().toLowerCase();
        if (value.length() > MAX_LENGTH || !FORMAT.matcher(value).matches()) {
            throw new InvalidDataException("El email no tiene un formato valido.");
        }
    }
}