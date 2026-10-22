package application.domain.ports.out;

import java.math.BigDecimal;

/**
 * Configurable business parameters of the marketplace.
 */
public interface BusinessConfigurationPort {

    /** Tax rate included in prices (e.g. 0.19). */
    BigDecimal getTaxRate();

    /** Days after delivery during which a return can be requested. */
    int getReturnWindowDays();

    /** Minutes an order may stay in PENDING_PAYMENT before it expires. */
    int getPaymentExpirationMinutes();

    /** Available quantity at or below which a stock record is reported as low stock. */
    int getLowStockThreshold();
}
