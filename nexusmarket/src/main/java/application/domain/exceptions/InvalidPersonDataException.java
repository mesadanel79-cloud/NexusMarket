package application.domain.exceptions;

public class InvalidPersonDataException extends DomainException {

    public InvalidPersonDataException(String message) {
        super(message);
    }
}
