package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Product;

import java.util.List;

public interface PublicAccessPort {

    Buyer registerBuyer(RegisterBuyerCommand command);

    List<Product> consultPublishedProducts();

    record RegisterBuyerCommand(String document, String fullName, String email,
                                String rawPassword, String primaryAddress) {
    }
}