package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.ProductType;

import java.math.BigDecimal;

public interface SellerPort {

    Product registerProduct(User actor, RegisterProductCommand command);

    Product publishProduct(User actor, Long productId);

    Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity);

    record RegisterProductCommand(String name, ProductType type, BigDecimal price) {
    }
}