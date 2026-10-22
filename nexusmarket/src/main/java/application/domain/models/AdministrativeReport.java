package application.domain.models;

import application.domain.valueobjects.Money;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Consolidated, read-only snapshot of the marketplace for administrative consultation
 * (OBJ-12). It is calculated on demand and never persisted.
 */
public class AdministrativeReport {

    private LocalDateTime generatedAt;
    private Map<String, Long> usersByRole;
    private Map<String, Long> ordersByStatus;
    private Map<String, Long> productsByStatus;
    private Map<String, Long> returnsByStatus;
    private Money totalInvoiced;
    private Money totalRefunded;
    private List<InventoryItem> lowStockItems;

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }

    public void setUsersByRole(Map<String, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public Map<String, Long> getProductsByStatus() {
        return productsByStatus;
    }

    public void setProductsByStatus(Map<String, Long> productsByStatus) {
        this.productsByStatus = productsByStatus;
    }

    public Map<String, Long> getReturnsByStatus() {
        return returnsByStatus;
    }

    public void setReturnsByStatus(Map<String, Long> returnsByStatus) {
        this.returnsByStatus = returnsByStatus;
    }

    public Money getTotalInvoiced() {
        return totalInvoiced;
    }

    public void setTotalInvoiced(Money totalInvoiced) {
        this.totalInvoiced = totalInvoiced;
    }

    public Money getTotalRefunded() {
        return totalRefunded;
    }

    public void setTotalRefunded(Money totalRefunded) {
        this.totalRefunded = totalRefunded;
    }

    public List<InventoryItem> getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(List<InventoryItem> lowStockItems) {
        this.lowStockItems = lowStockItems;
    }
}
