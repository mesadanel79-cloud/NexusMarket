package application.domain.models;

import application.domain.exceptions.InvalidInvoiceException;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Invoice issued when an order is paid. Prices are tax-inclusive: the total equals
 * the order total and is split into subtotal and tax using the configured tax rate.
 */
public class Invoice extends MarketplaceAsset {

    private Order order;
    private Buyer buyer;
    private LocalDateTime issueDate;
    private Money subtotal;
    private Money taxAmount;
    private Money total;
    private Money refundedAmount;
    private InvoiceStatus status;

    public static Invoice issueFor(Order order, BigDecimal taxRate) {
        if (order == null || !OrderStatus.PAID.equals(order.getStatus())) {
            throw new InvalidInvoiceException("Invoices can only be issued for paid orders.");
        }
        if (taxRate == null || taxRate.signum() < 0) {
            throw new InvalidInvoiceException("Tax rate must be zero or positive.");
        }
        Money total = order.getTotalAmount();
        BigDecimal net = total.getAmount().divide(BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP);
        Invoice invoice = new Invoice();
        invoice.order = order;
        invoice.buyer = order.getBuyer();
        invoice.issueDate = LocalDateTime.now();
        invoice.total = total;
        invoice.subtotal = Money.of(net, total.getCurrency());
        invoice.taxAmount = total.subtract(invoice.subtotal);
        invoice.refundedAmount = Money.zero(total.getCurrency());
        invoice.status = InvoiceStatus.ISSUED;
        return invoice;
    }

    /**
     * Registers a refund against the invoice. The accumulated refunds can never exceed the total.
     */
    public void registerRefund(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new InvalidInvoiceException("Refund amount must be greater than zero.");
        }
        Money accumulated = refundedAmount.add(amount);
        if (accumulated.isGreaterThan(total)) {
            throw new InvalidInvoiceException("Refunds cannot exceed the invoice total.");
        }
        this.refundedAmount = accumulated;
        this.status = accumulated.equals(total) ? InvoiceStatus.REFUNDED : InvoiceStatus.PARTIALLY_REFUNDED;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Money subtotal) {
        this.subtotal = subtotal;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public Money getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(Money refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
