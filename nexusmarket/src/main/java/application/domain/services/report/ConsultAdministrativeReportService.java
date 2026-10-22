package application.domain.services.report;

import application.domain.models.AdministrativeReport;
import application.domain.models.Invoice;
import application.domain.models.InventoryItem;
import application.domain.models.User;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.DomainCatalog;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consolidates administrative information for consultation (OBJ-12): users by role,
 * orders, products and returns by status, invoiced and refunded totals and low-stock
 * records. Read-only; allowed to SUPERVISOR and ADMIN.
 */
public class ConsultAdministrativeReportService {

    private final UserRepositoryPort userRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultAdministrativeReportService(UserRepositoryPort userRepositoryPort,
                                              OrderRepositoryPort orderRepositoryPort,
                                              ProductRepositoryPort productRepositoryPort,
                                              ReturnRequestRepositoryPort returnRequestRepositoryPort,
                                              InvoiceRepositoryPort invoiceRepositoryPort,
                                              InventoryRepositoryPort inventoryRepositoryPort,
                                              BusinessConfigurationPort businessConfigurationPort,
                                              ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.userRepositoryPort = userRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public AdministrativeReport generate(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.SUPERVISOR, UserRole.ADMIN);
        AdministrativeReport report = new AdministrativeReport();
        report.setGeneratedAt(LocalDateTime.now());
        report.setUsersByRole(countBy(userRepositoryPort.findAll(), User::getRole));
        report.setOrdersByStatus(countBy(orderRepositoryPort.findAll(), o -> o.getStatus()));
        report.setProductsByStatus(countBy(productRepositoryPort.findAll(), p -> p.getStatus()));
        report.setReturnsByStatus(countBy(returnRequestRepositoryPort.findAll(), r -> r.getStatus()));

        List<Invoice> invoices = invoiceRepositoryPort.findAll();
        Currency currency = invoices.isEmpty() ? Currency.COP : invoices.get(0).getTotal().getCurrency();
        Money invoiced = Money.zero(currency);
        Money refunded = Money.zero(currency);
        for (Invoice invoice : invoices) {
            if (invoice.getTotal().hasSameCurrency(invoiced)) {
                invoiced = invoiced.add(invoice.getTotal());
                refunded = refunded.add(invoice.getRefundedAmount());
            }
        }
        report.setTotalInvoiced(invoiced);
        report.setTotalRefunded(refunded);

        int threshold = businessConfigurationPort.getLowStockThreshold();
        List<InventoryItem> lowStock = inventoryRepositoryPort.findAll().stream()
                .filter(item -> item.getAvailableQuantity() <= threshold)
                .toList();
        report.setLowStockItems(lowStock);
        return report;
    }

    private static <T> Map<String, Long> countBy(List<T> elements, Function<T, ? extends DomainCatalog> classifier) {
        return elements.stream()
                .map(classifier)
                .filter(value -> value != null)
                .collect(Collectors.groupingBy(DomainCatalog::getCode, TreeMap::new, Collectors.counting()));
    }
}
