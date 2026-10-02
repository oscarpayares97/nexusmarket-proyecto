package application.domain.exceptions;

public class InvalidDataException extends DomainException {

    public InvalidDataException(String message) {
        super(message);
    }
}