package application.domain.models;

public record AuthenticationResult(String token, User user) {

    public AuthenticationResult {
        Guard.requireText(token, "The token");
        Guard.requireNonNull(user, "The user");
    }
}