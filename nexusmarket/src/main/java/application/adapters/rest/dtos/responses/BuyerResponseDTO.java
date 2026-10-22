package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class BuyerResponseDTO {

    private Long userId;
    private String primaryAddress;
    private List<String> additionalAddresses;
    private String commercialStatus;
}