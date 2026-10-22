package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class OwnedProductLoader {

    private final ProductRepositoryPort productRepository;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public OwnedProductLoader(ProductRepositoryPort productRepository,
                              ValidateRoleAuthorizationService authorization,
                              ValidateOwnershipService ownership) {
        this.productRepository = productRepository;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    public Product load(User actor, Long productId) {
        authorization.requireRole(actor, UserRole.SELLER);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
        ownership.requireOwner(actor, product.getSellerId());
        return product;
    }
}