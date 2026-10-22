package application.adapters.persistence.jpa.mappers;

import application.domain.models.InventoryItem;
import application.domain.models.Invoice;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.Warehouse;

/**
 * Converts a {@link MarketplaceAsset} to a (type, identifier) pair and back. Used by
 * the operation log (SQL) and the audit trail (MongoDB).
 */
public final class AssetReferences {

    private AssetReferences() {
    }

    public static String typeOf(MarketplaceAsset asset) {
        if (asset instanceof Warehouse) {
            return "WAREHOUSE";
        }
        if (asset instanceof Product) {
            return "PRODUCT";
        }
        if (asset instanceof InventoryItem) {
            return "INVENTORY_ITEM";
        }
        if (asset instanceof Order) {
            return "ORDER";
        }
        if (asset instanceof Invoice) {
            return "INVOICE";
        }
        if (asset instanceof Shipment) {
            return "SHIPMENT";
        }
        if (asset instanceof ReturnRequest) {
            return "RETURN_REQUEST";
        }
        return asset == null ? null : asset.getClass().getSimpleName().toUpperCase();
    }

    public static MarketplaceAsset reference(String type, String identifier) {
        if (type == null || identifier == null) {
            return null;
        }
        MarketplaceAsset asset = switch (type) {
            case "WAREHOUSE" -> new Warehouse();
            case "PRODUCT" -> new Product();
            case "INVENTORY_ITEM" -> new InventoryItem();
            case "ORDER" -> new Order();
            case "INVOICE" -> new Invoice();
            case "SHIPMENT" -> new Shipment();
            case "RETURN_REQUEST" -> new ReturnRequest();
            default -> null;
        };
        if (asset != null) {
            asset.setIdentifier(identifier);
        }
        return asset;
    }
}
