package application.domain.exceptions;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entity, String identifier) {
        super(entity + " not found" + (identifier == null || identifier.isBlank() ? "." : " with identifier '" + identifier + "'."));
    }

    public EntityNotFoundException(String entity) {
        this(entity, null);
    }
}
