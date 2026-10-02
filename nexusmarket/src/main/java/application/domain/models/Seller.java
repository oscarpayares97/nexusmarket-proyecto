package application.domain.models;

public class Seller {

    private final Long userId;
    private final String tradeName;

    public Seller(Long userId, String tradeName) {
        Guard.requireNonNull(userId, "El usuario");
        this.userId = userId;
        this.tradeName = Guard.requireText(tradeName, "El nombre comercial");
    }

    public Long getUserId() { return userId; }
    public String getTradeName() { return tradeName; }
}