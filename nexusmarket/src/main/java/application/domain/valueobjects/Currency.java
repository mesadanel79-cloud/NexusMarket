package application.domain.valueobjects;

import java.util.List;

/**
 * Currency in which marketplace prices are expressed.
 */
public final class Currency extends DomainCatalog {

    public static final Currency COP = new Currency("COP", "Colombian Peso", "Colombian peso.");
    public static final Currency USD = new Currency("USD", "United States Dollar", "United States dollar.");

    private static final List<Currency> VALUES = List.of(COP, USD);

    private Currency(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<Currency> values() {
        return VALUES;
    }

    public static Currency fromCode(String code) {
        return resolve(VALUES, code, "Currency");
    }
}
