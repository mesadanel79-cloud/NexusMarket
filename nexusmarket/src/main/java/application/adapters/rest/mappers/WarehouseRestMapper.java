package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.requests.WarehouseRequestDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;

public final class WarehouseRestMapper {

    private WarehouseRestMapper() {
    }

    public static Warehouse toDomain(WarehouseRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(dto.code());
        warehouse.setName(dto.name());
        warehouse.setCity(dto.city());
        warehouse.setAddress(dto.address());
        warehouse.setWarehouseType(WarehouseType.fromCode(dto.warehouseType()));
        if (dto.ownerSellerIdentification() != null && !dto.ownerSellerIdentification().isBlank()) {
            warehouse.setOwner(ParticipantRestMapper.sellerReference(dto.ownerSellerIdentification()));
        }
        return warehouse;
    }

    public static Warehouse reference(String code) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(code);
        return warehouse;
    }

    public static WarehouseResponseDTO toResponse(Warehouse warehouse) {
        return new WarehouseResponseDTO(warehouse.getIdentifier(), warehouse.getName(), warehouse.getCity(),
                warehouse.getAddress(), code(warehouse.getWarehouseType()),
                warehouse.getOwner() == null ? null : warehouse.getOwner().getIdentification(),
                code(warehouse.getStatus()));
    }
}
