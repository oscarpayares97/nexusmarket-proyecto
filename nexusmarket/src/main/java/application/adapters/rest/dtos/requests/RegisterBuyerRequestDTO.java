package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterBuyerRequestDTO {

    @NotBlank
    @Size(max = 20)
    private String document;

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 200)
    private String primaryAddress;
}