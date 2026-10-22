package application.domain.services.operation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small builder for the variable {@code details} document of an audit entry.
 * Null values are skipped.
 */
public final class AuditDetails {

    private AuditDetails() {
    }

    public static Map<String, Object> of(Object... keyValues) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            Object value = keyValues[i + 1];
            if (value != null) {
                details.put(String.valueOf(keyValues[i]), value instanceof String || value instanceof Number
                        || value instanceof Boolean ? value : String.valueOf(value));
            }
        }
        return details;
    }
}
