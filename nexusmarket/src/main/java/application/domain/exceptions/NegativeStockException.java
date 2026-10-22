package application.domain.exceptions;

public class NegativeStockException extends DomainException {

    public NegativeStockException(String message) {
        super(message);
    }
}
