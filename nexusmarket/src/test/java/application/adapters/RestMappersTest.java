package application.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.adapters.rest.dtos.requests.CreateProductRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.requests.VariantRequestDTO;
import application.adapters.rest.mappers.CatalogRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class RestMappersTest {

    @Test
    void createProductRequestBecomesDomainProduct() {
        CreateProductRequestDTO dto = new CreateProductRequestDTO("Laptop", "desc", "ELECTRONICS", "PHYSICAL",
                List.of(new VariantRequestDTO("LAP-1", "8GB", new BigDecimal("100"), "COP")));
        Product product = CatalogRestMapper.toDomain(dto);
        assertEquals(ProductCategory.ELECTRONICS, product.getCategory());
        assertEquals(ProductType.PHYSICAL, product.getProductType());
        assertEquals("LAP-1", product.getVariants().get(0).getSku());
    }

    @Test
    void registerBuyerRequestBuildsUserWithBuyerProfile() {
        RegisterBuyerRequestDTO dto = new RegisterBuyerRequestDTO("1017", "Ana", "ana@mail.com", "3001234567",
                "Main 1", List.of("Office 2"), "ana.buyer", "Password123*");
        User user = UserRestMapper.toNewBuyerUser(dto);
        assertEquals("1017", user.getBuyer().getIdentification());
        assertEquals(1, user.getBuyer().getAdditionalAddresses().size());
    }
}
