package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;

public class SuspendProductService {

    private final OwnedProductLoader loader;
    private final ProductRepositoryPort productRepository;

    public SuspendProductService(OwnedProductLoader loader, ProductRepositoryPort productRepository) {
        this.loader = loader;
        this.productRepository = productRepository;
    }

    public Product suspend(User actor, Long productId) {
        Product product = loader.load(actor, productId);
        product.suspend();
        return productRepository.save(product);
    }
}