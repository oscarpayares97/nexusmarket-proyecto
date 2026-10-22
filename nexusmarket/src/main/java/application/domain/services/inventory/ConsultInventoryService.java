package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultInventoryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final ProductRepositoryPort productRepository;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public ConsultInventoryService(InventoryRepositoryPort inventoryRepository,
                                   ProductRepositoryPort productRepository,
                                   ValidateRoleAuthorizationService authorization,
                                   ValidateOwnershipService ownership) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    public List<Inventory> consultInventory(User actor, Long productId) {
        authorization.requireRole(actor, UserRole.SELLER, UserRole.LOGISTICS_OPERATOR,
                UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
        if (actor.getRole() == UserRole.SELLER) {
            ownership.requireOwner(actor, product.getSellerId());
        }
        return inventoryRepository.findByProductId(productId);
    }
}