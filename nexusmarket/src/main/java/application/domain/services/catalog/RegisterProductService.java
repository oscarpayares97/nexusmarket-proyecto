package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.UserRole;
import application.domain.ports.in.SellerPort.RegisterProductCommand;
public class RegisterProductService {

    private final ProductRepositoryPort productRepository;
    private final ValidateRoleAuthorizationService authorization;

    public RegisterProductService(ProductRepositoryPort productRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.productRepository = productRepository;
        this.authorization = authorization;
    }

    public Product register(User actor, RegisterProductCommand command) {
        authorization.requireRole(actor, UserRole.SELLER);
        Product product = new Product(null, actor.getId(), command.name(),
                command.type(), new Money(command.price()));
        return productRepository.save(product);
    }
}