package application.domain.exceptions;

public class DuplicateEntityException extends DomainException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}