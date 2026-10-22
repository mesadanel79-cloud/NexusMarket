package application.domain.exceptions;

public class OrderImmutableException extends DomainException {

    public OrderImmutableException(String message) {
        super(message);
    }
}
