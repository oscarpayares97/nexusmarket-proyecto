package application.domain.ports.out;

import application.domain.models.User;

public interface JwtServicePort {

    String generate(User user);

    // Lanza InvalidCredentialsException si el token no es válido
    Long extractUserId(String token);
}