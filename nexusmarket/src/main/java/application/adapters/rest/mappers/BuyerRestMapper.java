package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.domain.models.Buyer;
import application.domain.ports.in.PublicAccessPort;

public final class BuyerRestMapper {

    private BuyerRestMapper() {
    }

    public static PublicAccessPort.RegisterBuyerCommand toCommand(RegisterBuyerRequestDTO dto) {
        return new PublicAccessPort.RegisterBuyerCommand(dto.getDocument(), dto.getFullName(),
                dto.getEmail(), dto.getPassword(), dto.getPrimaryAddress());
    }

    public static BuyerResponseDTO toResponseDTO(Buyer buyer) {
        BuyerResponseDTO dto = new BuyerResponseDTO();
        dto.setUserId(buyer.getUserId());
        dto.setPrimaryAddress(buyer.getPrimaryAddress());
        dto.setAdditionalAddresses(buyer.getAdditionalAddresses());
        dto.setCommercialStatus(buyer.getCommercialStatus().name());
        return dto;
    }
}