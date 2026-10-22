package application.domain.exceptions;

public class InvalidStatusTransitionException extends DomainException {

    public InvalidStatusTransitionException(String entity, String from, String to) {
        super("Invalid " + entity + " status transition from " + from + " to " + to + ".");
    }
}
