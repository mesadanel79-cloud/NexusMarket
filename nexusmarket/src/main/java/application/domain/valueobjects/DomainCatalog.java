package application.domain.valueobjects;

import application.domain.exceptions.InvalidCatalogValueException;
import java.util.List;
import java.util.Objects;

/**
 * Base class of the bounded catalog Value Objects (statuses, roles, types).
 *
 * <p>Each catalog value is an immutable singleton identified by its {@code code}.
 * Two catalog values are equal only when they belong to the same catalog class and
 * share the same code, so {@code UserStatus.ACTIVE} is never equal to
 * {@code BuyerStatus.ACTIVE}.
 */
public abstract class DomainCatalog {

    private final String code;
    private final String name;
    private final String description;

    protected DomainCatalog(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    protected static <T extends DomainCatalog> T resolve(List<T> values, String code, String catalogName) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim().toUpperCase();
        for (T value : values) {
            if (value.getCode().equals(normalized)) {
                return value;
            }
        }
        throw new InvalidCatalogValueException(catalogName, code);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return code.equals(((DomainCatalog) other).code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName(), code);
    }

    @Override
    public String toString() {
        return code;
    }
}
