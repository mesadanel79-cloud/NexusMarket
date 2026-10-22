package application.domain.exceptions;

public class InvalidCatalogValueException extends DomainException {

    public InvalidCatalogValueException(String catalog, String code) {
        super("Unknown " + catalog + " code: '" + code + "'.");
    }
}
