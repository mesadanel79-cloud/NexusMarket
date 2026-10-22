package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.services.buyer.ConsultBuyerProfileService;
import application.domain.services.buyer.UpdateBuyerProfileService;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.ManageCartService;
import application.domain.services.invoice.ConsultInvoicesService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.CheckoutService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.order.PayOrderService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.shipment.ConsultShipmentsService;
import application.domain.services.user.ChangePasswordService;
import application.domain.valueobjects.PaymentMethod;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuyerUseCaseImpl implements BuyerPort {

    private final ConsultBuyerProfileService consultBuyerProfileService;
    private final UpdateBuyerProfileService updateBuyerProfileService;
    private final ChangePasswordService changePasswordService;
    private final ConsultCartService consultCartService;
    private final ManageCartService manageCartService;
    private final CheckoutService checkoutService;
    private final PayOrderService payOrderService;
    private final CancelOrderService cancelOrderService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultInvoicesService consultInvoicesService;
    private final RequestReturnService requestReturnService;
    private final ConsultReturnsService consultReturnsService;

    public BuyerUseCaseImpl(ConsultBuyerProfileService consultBuyerProfileService,
                            UpdateBuyerProfileService updateBuyerProfileService,
                            ChangePasswordService changePasswordService,
                            ConsultCartService consultCartService, ManageCartService manageCartService,
                            CheckoutService checkoutService, PayOrderService payOrderService,
                            CancelOrderService cancelOrderService, ConsultOrdersService consultOrdersService,
                            ConsultShipmentsService consultShipmentsService,
                            ConsultInvoicesService consultInvoicesService,
                            RequestReturnService requestReturnService, ConsultReturnsService consultReturnsService) {
        this.consultBuyerProfileService = consultBuyerProfileService;
        this.updateBuyerProfileService = updateBuyerProfileService;
        this.changePasswordService = changePasswordService;
        this.consultCartService = consultCartService;
        this.manageCartService = manageCartService;
        this.checkoutService = checkoutService;
        this.payOrderService = payOrderService;
        this.cancelOrderService = cancelOrderService;
        this.consultOrdersService = consultOrdersService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultInvoicesService = consultInvoicesService;
        this.requestReturnService = requestReturnService;
        this.consultReturnsService = consultReturnsService;
    }

    @Override
    @Transactional(readOnly = true)
    public Buyer consultMyProfile(User user) {
        return consultBuyerProfileService.consult(user);
    }

    @Override
    @Transactional
    public Buyer updateMyProfile(User user, Buyer buyer) {
        return updateBuyerProfileService.update(user, buyer);
    }

    @Override
    @Transactional
    public void changeMyPassword(User user, String currentPassword, String newPassword) {
        changePasswordService.changePassword(user, currentPassword, newPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart consultMyCart(User user) {
        return consultCartService.consult(user);
    }

    @Override
    @Transactional
    public Cart addItemToCart(User user, CartItem item) {
        return manageCartService.addItem(user, item);
    }

    @Override
    @Transactional
    public Cart updateCartItem(User user, CartItem item) {
        return manageCartService.updateItem(user, item);
    }

    @Override
    @Transactional
    public Cart removeCartItem(User user, CartItem item) {
        return manageCartService.removeItem(user, item);
    }

    @Override
    @Transactional
    public Order checkout(User user, String shippingAddress) {
        return checkoutService.checkout(user, shippingAddress);
    }

    @Override
    @Transactional
    public Order payOrder(User user, Order order, PaymentMethod paymentMethod) {
        return payOrderService.pay(user, order, paymentMethod);
    }

    @Override
    @Transactional
    public Order cancelOrder(User user, Order order) {
        return cancelOrderService.cancel(user, order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> consultMyOrders(User user) {
        return consultOrdersService.consultBuyerOrders(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Order consultMyOrder(User user, Order order) {
        return consultOrdersService.consultBuyerOrder(user, order);
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment consultOrderShipment(User user, Order order) {
        return consultShipmentsService.consultOrderShipment(user, order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> consultMyInvoices(User user) {
        return consultInvoicesService.consultBuyerInvoices(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice consultMyInvoice(User user, Invoice invoice) {
        return consultInvoicesService.consultBuyerInvoice(user, invoice);
    }

    @Override
    @Transactional
    public ReturnRequest requestReturn(User user, ReturnRequest returnRequest) {
        return requestReturnService.request(user, returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> consultMyReturns(User user) {
        return consultReturnsService.consultBuyerReturns(user);
    }
}
