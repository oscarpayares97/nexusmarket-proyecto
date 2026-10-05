package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;

public interface RegisterSellerUseCase {

    Seller register(User actor, Command command);

    record Command(String document, String fullName, String email, String rawPassword,
                   String tradeName, String warehouseName, String warehouseLocation) {
    }
}