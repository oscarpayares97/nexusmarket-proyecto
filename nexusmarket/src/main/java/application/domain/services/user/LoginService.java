package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;

public class LoginService {

    private final UserRepositoryPort userRepository;
    private final PasswordServicePort passwordService;
    private final JwtServicePort jwtService;

    public LoginService(UserRepositoryPort userRepository, PasswordServicePort passwordService,
                        JwtServicePort jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public AuthenticationResult login(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
        User user = userRepository.findByEmail(email)
                .filter(found -> passwordService.matches(rawPassword, found.getPasswordHash()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));
        if (!user.isActive()) {
            throw new UnauthorizedOperationException("The user is not active.");
        }
        return new AuthenticationResult(jwtService.generate(user), user);
    }
}