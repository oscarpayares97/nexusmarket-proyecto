package application.domain.valueobjects;

import application.domain.exceptions.InvalidDataException;

import java.util.regex.Pattern;

public record DocumentNumber(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[A-Za-z0-9-]{1,20}$");

    public DocumentNumber {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException("El documento es requerido.");
        }
        value = value.trim();
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidDataException("El documento debe contener como máximo 20 letras, números o guiones.");
        }
    }
}