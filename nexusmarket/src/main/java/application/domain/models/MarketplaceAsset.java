package application.domain.models;

/**
 * Abstract business asset of the marketplace that can be affected by operations and
 * referenced by the audit trail (warehouses, products, stock items, orders, invoices,
 * shipments and return requests).
 */
public abstract class MarketplaceAsset {

    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
