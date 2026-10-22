package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.requests.UpdateBuyerProfileRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of the commercial participants (buyers and sellers).
 */
public final class ParticipantRestMapper {

    private ParticipantRestMapper() {
    }

    public static Buyer toDesiredBuyer(UpdateBuyerProfileRequestDTO dto) {
        Buyer buyer = new Buyer();
        buyer.setName(dto.name());
        buyer.setEmail(dto.email());
        buyer.setPhoneNumber(dto.phoneNumber());
        buyer.setAddress(dto.address());
        buyer.setAdditionalAddresses(dto.additionalAddresses() == null
                ? new ArrayList<>() : new ArrayList<>(dto.additionalAddresses()));
        return buyer;
    }

    public static Buyer buyerReference(String identification) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(identification);
        return buyer;
    }

    public static Seller sellerReference(String identification) {
        Seller seller = new Seller();
        seller.setIdentification(identification);
        return seller;
    }

    public static BuyerResponseDTO toResponse(Buyer buyer) {
        return new BuyerResponseDTO(buyer.getIdentification(), buyer.getName(), buyer.getEmail(),
                buyer.getPhoneNumber(), buyer.getAddress(), List.copyOf(buyer.getAdditionalAddresses()),
                code(buyer.getStatus()));
    }

    public static SellerResponseDTO toResponse(Seller seller) {
        return new SellerResponseDTO(seller.getIdentification(), seller.getName(), seller.getEmail(),
                seller.getPhoneNumber(), seller.getAddress(), seller.getStoreName(), code(seller.getStatus()),
                seller.getRegistrationDate());
    }
}
