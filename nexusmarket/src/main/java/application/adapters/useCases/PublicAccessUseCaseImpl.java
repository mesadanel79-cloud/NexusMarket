package application.adapters.useCases;

import application.domain.models.AuthenticationResult;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.user.LoginService;
import application.domain.services.user.LogoutService;
import application.domain.services.user.RegisterBuyerService;
import application.domain.valueobjects.ProductCategory;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterBuyerService registerBuyerService;
    private final ConsultCatalogService consultCatalogService;

    public PublicAccessUseCaseImpl(LoginService loginService, LogoutService logoutService,
                                   RegisterBuyerService registerBuyerService,
                                   ConsultCatalogService consultCatalogService) {
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.registerBuyerService = registerBuyerService;
        this.consultCatalogService = consultCatalogService;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResult login(User credentials) {
        return loginService.login(credentials);
    }

    @Override
    @Transactional
    public void logout(User user) {
        logoutService.logout(user);
    }

    @Override
    @Transactional
    public User registerBuyer(User newBuyerUser) {
        return registerBuyerService.register(newBuyerUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> consultCatalog(ProductCategory category) {
        return consultCatalogService.consultPublished(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Product consultProduct(Product product) {
        return consultCatalogService.consultPublishedProduct(product);
    }
}
