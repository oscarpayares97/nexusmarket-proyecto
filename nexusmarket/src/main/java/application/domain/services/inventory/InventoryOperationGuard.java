package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.UserRole;

import java.util.Optional;

public class InventoryOperationGuard {

    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final ValidateRoleAuthorizationService authorization;

    public InventoryOperationGuard(ProductRepositoryPort productRepository,
                                   WarehouseRepositoryPort warehouseRepository,
                                   InventoryRepositoryPort inventoryRepository,
                                   ValidateRoleAuthorizationService authorization) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.authorization = authorization;
    }

    public Inventory load(User actor, Long productId, Long warehouseId, boolean createIfMissing) {
        authorization.requireRole(actor, UserRole.SELLER, UserRole.LOGISTICS_OPERATOR);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Almacen no encontrado."));

        if (!product.isPhysical()) {
            throw new InvalidDataException("Los productos digitales no requieren gestión de inventario.");
        }
        if (!warehouse.isActive()) {
            throw new InvalidDataException("El almacen esta inactivo.");
        }
        if (actor.getRole() == UserRole.SELLER) {
            if (!actor.getId().equals(product.getSellerId())) {
                throw new UnauthorizedOperationException(
                        "Un vendedor solo puede gestionar el stock de sus propios productos.");
            }
            if (!actor.getId().equals(warehouse.getSellerId())) {
                throw new UnauthorizedOperationException("Un vendedor solo puede operar en sus propios almacenes.");
            }
        }

        Optional<Inventory> existing = inventoryRepository.findByProductAndWarehouse(productId, warehouseId);
        if (existing.isPresent()) {
            return existing.get();
        }
        if (createIfMissing) {
            return new Inventory(null, productId, warehouseId);
        }
        throw new EntityNotFoundException("No hay existencias de ese producto en ese almacén.");
    }

    public void recordMovement(MovementType type, Inventory inventory, int quantity, User actor) {
        inventoryRepository.saveMovement(new InventoryMovement(null, type,
                inventory.getProductId(), inventory.getWarehouseId(), quantity, actor.getId()));
    }
}