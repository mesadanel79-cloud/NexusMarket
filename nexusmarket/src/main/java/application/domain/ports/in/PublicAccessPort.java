package application.domain.ports.in;

import application.domain.models.AuthenticationResult;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.ProductCategory;
import java.util.List;

/**
 * Operations available without authentication (plus logout).
 */
public interface PublicAccessPort {

    AuthenticationResult login(User credentials);

    void logout(User user);

    User registerBuyer(User newBuyerUser);

    List<Product> consultCatalog(ProductCategory category);

    Product consultProduct(Product product);
}
