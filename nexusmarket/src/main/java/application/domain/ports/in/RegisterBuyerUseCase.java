package application.domain.ports.in;

import application.domain.models.Buyer;

public interface RegisterBuyerUseCase {

    Buyer register(Command command);

    record Command(String document, String fullName, String email,
                   String rawPassword, String primaryAddress) {
    }
}