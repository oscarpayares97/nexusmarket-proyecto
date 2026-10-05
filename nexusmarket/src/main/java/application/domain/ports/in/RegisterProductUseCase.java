package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.ProductType;

import java.math.BigDecimal;

public interface RegisterProductUseCase {

    Product register(User actor, Command command);

    record Command(String name, ProductType type, BigDecimal price) {
    }
}