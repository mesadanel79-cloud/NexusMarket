package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterStaffUserRequestDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserRole;
import java.util.ArrayList;

public final class UserRestMapper {

    private UserRestMapper() {
    }

    public static User toCredentials(LoginRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        return user;
    }

    public static User toNewBuyerUser(RegisterBuyerRequestDTO dto) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(dto.identification());
        buyer.setName(dto.name());
        buyer.setEmail(dto.email());
        buyer.setPhoneNumber(dto.phoneNumber());
        buyer.setAddress(dto.address());
        buyer.setAdditionalAddresses(dto.additionalAddresses() == null
                ? new ArrayList<>() : new ArrayList<>(dto.additionalAddresses()));
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setBuyer(buyer);
        return user;
    }

    public static User toNewSellerUser(RegisterSellerRequestDTO dto) {
        Seller seller = new Seller();
        seller.setIdentification(dto.identification());
        seller.setName(dto.name());
        seller.setEmail(dto.email());
        seller.setPhoneNumber(dto.phoneNumber());
        seller.setAddress(dto.address());
        seller.setStoreName(dto.storeName());
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setSeller(seller);
        return user;
    }

    public static Warehouse toFirstWarehouse(RegisterSellerRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(dto.firstWarehouse().code());
        warehouse.setName(dto.firstWarehouse().name());
        warehouse.setCity(dto.firstWarehouse().city());
        warehouse.setAddress(dto.firstWarehouse().address());
        return warehouse;
    }

    public static User toNewStaffUser(RegisterStaffUserRequestDTO dto) {
        User user = new User();
        user.setIdentification(dto.identification());
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setAddress(dto.address());
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setRole(UserRole.fromCode(dto.role()));
        return user;
    }

    public static User reference(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    public static UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getIdentification(), user.getName(),
                user.getEmail(), code(user.getRole()), code(user.getStatus()),
                user.getBuyer() == null ? null : user.getBuyer().getIdentification(),
                user.getSeller() == null ? null : user.getSeller().getIdentification());
    }

    public static LoginResponseDTO toLoginResponse(AuthenticationResult result, long expiresInSeconds) {
        return new LoginResponseDTO(result.getToken(), "Bearer", expiresInSeconds,
                toResponse(result.getAuthenticatedUser()));
    }

    static String code(application.domain.valueobjects.DomainCatalog value) {
        return value == null ? null : value.getCode();
    }
}
