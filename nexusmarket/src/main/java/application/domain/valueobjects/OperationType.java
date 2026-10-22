package application.domain.valueobjects;

import java.util.List;

/**
 * Type of significant business operation recorded in the operation log and the audit trail.
 */
public final class OperationType extends DomainCatalog {
    public static final OperationType USER_REGISTRATION = new OperationType(
            "USER_REGISTRATION", "User Registration", "Registration of a system user.");
    public static final OperationType USER_STATUS_CHANGE = new OperationType(
            "USER_STATUS_CHANGE", "User Status Change", "User blocked or unblocked.");
    public static final OperationType USER_PASSWORD_CHANGE = new OperationType(
            "USER_PASSWORD_CHANGE", "User Password Change", "User password changed.");
    public static final OperationType BUYER_REGISTRATION = new OperationType(
            "BUYER_REGISTRATION", "Buyer Registration", "Registration of a buyer.");
    public static final OperationType BUYER_UPDATE = new OperationType(
            "BUYER_UPDATE", "Buyer Update", "Update of buyer contact data.");
    public static final OperationType BUYER_STATUS_CHANGE = new OperationType(
            "BUYER_STATUS_CHANGE", "Buyer Status Change", "Change of the buyer commercial status.");
    public static final OperationType SELLER_REGISTRATION = new OperationType(
            "SELLER_REGISTRATION", "Seller Registration", "Registration of a seller by an administrator.");
    public static final OperationType SELLER_STATUS_CHANGE = new OperationType(
            "SELLER_STATUS_CHANGE", "Seller Status Change", "Change of the seller commercial status.");
    public static final OperationType WAREHOUSE_REGISTRATION = new OperationType(
            "WAREHOUSE_REGISTRATION", "Warehouse Registration", "Registration of a warehouse.");
    public static final OperationType WAREHOUSE_STATUS_CHANGE = new OperationType(
            "WAREHOUSE_STATUS_CHANGE", "Warehouse Status Change", "Warehouse activated or deactivated.");
    public static final OperationType PRODUCT_CREATION = new OperationType(
            "PRODUCT_CREATION", "Product Creation", "Creation and publication of a product.");
    public static final OperationType PRODUCT_VARIANT_ADDITION = new OperationType(
            "PRODUCT_VARIANT_ADDITION", "Product Variant Addition", "New variant added to a product.");
    public static final OperationType PRODUCT_PRICE_CHANGE = new OperationType(
            "PRODUCT_PRICE_CHANGE", "Product Price Change", "Price of a product variant changed.");
    public static final OperationType PRODUCT_SUSPENSION = new OperationType(
            "PRODUCT_SUSPENSION", "Product Suspension", "Product suspended by its seller.");
    public static final OperationType PRODUCT_REACTIVATION = new OperationType(
            "PRODUCT_REACTIVATION", "Product Reactivation", "Product published again.");
    public static final OperationType PRODUCT_DISCONTINUATION = new OperationType(
            "PRODUCT_DISCONTINUATION", "Product Discontinuation", "Product permanently discontinued.");
    public static final OperationType INVENTORY_INFLOW = new OperationType(
            "INVENTORY_INFLOW", "Inventory Inflow", "Stock received in a warehouse.");
    public static final OperationType INVENTORY_ADJUSTMENT = new OperationType(
            "INVENTORY_ADJUSTMENT", "Inventory Adjustment", "Manual stock adjustment.");
    public static final OperationType ORDER_CHECKOUT = new OperationType(
            "ORDER_CHECKOUT", "Order Checkout", "Order created from the cart and stock reserved.");
    public static final OperationType ORDER_PAYMENT = new OperationType(
            "ORDER_PAYMENT", "Order Payment", "Order paid, stock sold and invoice issued.");
    public static final OperationType ORDER_CANCELLATION = new OperationType(
            "ORDER_CANCELLATION", "Order Cancellation", "Order cancelled by the buyer.");
    public static final OperationType ORDER_EXPIRATION = new OperationType(
            "ORDER_EXPIRATION", "Order Expiration", "Order cancelled automatically because payment did not arrive in time.");
    public static final OperationType ORDER_DELIVERY = new OperationType(
            "ORDER_DELIVERY", "Order Delivery", "Order delivered.");
    public static final OperationType INVOICE_ISSUANCE = new OperationType(
            "INVOICE_ISSUANCE", "Invoice Issuance", "Invoice issued for a paid order.");
    public static final OperationType SHIPMENT_DISPATCH = new OperationType(
            "SHIPMENT_DISPATCH", "Shipment Dispatch", "Order dispatched to the carrier.");
    public static final OperationType SHIPMENT_DELIVERY = new OperationType(
            "SHIPMENT_DELIVERY", "Shipment Delivery", "Shipment delivered to the buyer.");
    public static final OperationType RETURN_REQUEST = new OperationType(
            "RETURN_REQUEST", "Return Request", "Return requested by a buyer.");
    public static final OperationType RETURN_APPROVAL = new OperationType(
            "RETURN_APPROVAL", "Return Approval", "Return approved by an administrator.");
    public static final OperationType RETURN_REJECTION = new OperationType(
            "RETURN_REJECTION", "Return Rejection", "Return rejected by an administrator.");
    public static final OperationType INVENTORY_DAMAGE = new OperationType(
            "INVENTORY_DAMAGE", "Inventory Damage", "Units marked as damaged; they can no longer be reserved.");
    public static final OperationType RETURN_RECEPTION = new OperationType(
            "RETURN_RECEPTION", "Return Reception", "Returned item received into a warehouse.");
    public static final OperationType REFUND_ISSUANCE = new OperationType(
            "REFUND_ISSUANCE", "Refund Issuance", "Refund issued to the buyer.");

    private static final List<OperationType> VALUES = List.of(
            USER_REGISTRATION, USER_STATUS_CHANGE, USER_PASSWORD_CHANGE, BUYER_REGISTRATION, BUYER_UPDATE, BUYER_STATUS_CHANGE, SELLER_REGISTRATION, SELLER_STATUS_CHANGE, WAREHOUSE_REGISTRATION, WAREHOUSE_STATUS_CHANGE, PRODUCT_CREATION, PRODUCT_VARIANT_ADDITION, PRODUCT_PRICE_CHANGE, PRODUCT_SUSPENSION, PRODUCT_REACTIVATION, PRODUCT_DISCONTINUATION, INVENTORY_INFLOW, INVENTORY_ADJUSTMENT, ORDER_CHECKOUT, ORDER_PAYMENT, ORDER_CANCELLATION, ORDER_EXPIRATION, ORDER_DELIVERY, INVOICE_ISSUANCE, SHIPMENT_DISPATCH, SHIPMENT_DELIVERY, RETURN_REQUEST, RETURN_APPROVAL, RETURN_REJECTION, INVENTORY_DAMAGE, RETURN_RECEPTION, REFUND_ISSUANCE);

    private OperationType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OperationType> values() {
        return VALUES;
    }

    public static OperationType fromCode(String code) {
        return resolve(VALUES, code, "OperationType");
    }
}
