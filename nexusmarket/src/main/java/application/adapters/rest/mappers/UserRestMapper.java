package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;

public final class UserRestMapper {

    private UserRestMapper() {
    }

    public static LoginResponseDTO toLoginResponse(AuthenticationResult result, long expiresInSeconds) {
        User user = result.user();
        LoginResponseDTO.UserInfo info = new LoginResponseDTO.UserInfo();
        info.setUserId(user.getId());
        info.setFullName(user.getFullName());
        info.setEmail(user.getEmail().value());
        info.setRole(user.getRole().name());

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(result.token());
        dto.setExpiresIn(expiresInSeconds);
        dto.setUser(info);
        return dto;
    }
}