package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.UserRepositoryPort;

public class LoadAuthenticatedUserService {

    private final JwtServicePort jwtService;
    private final UserRepositoryPort userRepository;

    public LoadAuthenticatedUserService(JwtServicePort jwtService, UserRepositoryPort userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public User load(String token) {
        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid token."));
        if (!user.isActive()) {
            throw new UnauthorizedOperationException("The user is not active.");
        }
        return user;
    }
}