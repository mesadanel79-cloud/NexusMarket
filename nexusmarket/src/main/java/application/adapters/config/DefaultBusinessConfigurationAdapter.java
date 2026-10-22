package application.adapters.config;

import application.domain.ports.out.BusinessConfigurationPort;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Business parameters read from application.properties (overridable by environment).
 */
@Component
public class DefaultBusinessConfigurationAdapter implements BusinessConfigurationPort {

    private final BigDecimal taxRate;
    private final int returnWindowDays;
    private final int paymentExpirationMinutes;
    private final int lowStockThreshold;

    public DefaultBusinessConfigurationAdapter(
            @Value("${business.tax-rate:0.19}") BigDecimal taxRate,
            @Value("${business.return-window-days:30}") int returnWindowDays,
            @Value("${business.payment-expiration-minutes:60}") int paymentExpirationMinutes,
            @Value("${business.low-stock-threshold:5}") int lowStockThreshold) {
        this.taxRate = taxRate;
        this.returnWindowDays = returnWindowDays;
        this.paymentExpirationMinutes = paymentExpirationMinutes;
        this.lowStockThreshold = lowStockThreshold;
    }

    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    @Override
    public int getReturnWindowDays() {
        return returnWindowDays;
    }

    @Override
    public int getPaymentExpirationMinutes() {
        return paymentExpirationMinutes;
    }

    @Override
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}
