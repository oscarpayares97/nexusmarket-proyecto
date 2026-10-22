package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.ProductType;

import java.math.BigDecimal;
import java.util.List;

public interface SellerPort {

    Product registerProduct(User actor, RegisterProductCommand command);

    Product publishProduct(User actor, Long productId);

    Product addProductVariant(User actor, Long productId, String variant);

    Product suspendProduct(User actor, Long productId);

    Product discontinueProduct(User actor, Long productId);

    Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity);

    Inventory adjustInventory(User actor, Long productId, Long warehouseId, int newQuantity);

    Inventory changeInventoryStatus(User actor, Long productId, Long warehouseId, InventoryStatus status);

    List<Inventory> consultInventory(User actor, Long productId);

    List<Warehouse> consultWarehouses(User actor);

    void changePassword(User actor, String currentPassword, String newPassword);

    record RegisterProductCommand(String name, ProductType type, BigDecimal price) {
    }
}