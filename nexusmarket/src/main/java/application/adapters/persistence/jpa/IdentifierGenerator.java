package application.adapters.persistence.jpa;

import java.util.UUID;

/**
 * Generates readable business identifiers (e.g. ORD-3F9A1C2B7D10) for aggregates
 * that do not receive one from the caller.
 */
final class IdentifierGenerator {

    private IdentifierGenerator() {
    }

    static String next(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
