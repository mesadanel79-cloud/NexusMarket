package application.domain.exceptions;

public class InvalidInventoryOperationException extends DomainException {

    public InvalidInventoryOperationException(String message) {
        super(message);
    }
}
