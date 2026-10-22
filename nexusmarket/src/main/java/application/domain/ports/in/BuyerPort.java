package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.PaymentMethod;
import java.util.List;

/**
 * Use cases of the BUYER role.
 */
public interface BuyerPort {

    Buyer consultMyProfile(User user);

    Buyer updateMyProfile(User user, Buyer buyer);

    void changeMyPassword(User user, String currentPassword, String newPassword);

    Cart consultMyCart(User user);

    Cart addItemToCart(User user, CartItem item);

    Cart updateCartItem(User user, CartItem item);

    Cart removeCartItem(User user, CartItem item);

    Order checkout(User user, String shippingAddress);

    Order payOrder(User user, Order order, PaymentMethod paymentMethod);

    Order cancelOrder(User user, Order order);

    List<Order> consultMyOrders(User user);

    Order consultMyOrder(User user, Order order);

    Shipment consultOrderShipment(User user, Order order);

    List<Invoice> consultMyInvoices(User user);

    Invoice consultMyInvoice(User user, Invoice invoice);

    ReturnRequest requestReturn(User user, ReturnRequest returnRequest);

    List<ReturnRequest> consultMyReturns(User user);
}
