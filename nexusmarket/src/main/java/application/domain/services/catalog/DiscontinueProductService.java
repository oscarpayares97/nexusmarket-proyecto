package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;

public class DiscontinueProductService {

    private final OwnedProductLoader loader;
    private final ProductRepositoryPort productRepository;

    public DiscontinueProductService(OwnedProductLoader loader, ProductRepositoryPort productRepository) {
        this.loader = loader;
        this.productRepository = productRepository;
    }

    public Product discontinue(User actor, Long productId) {
        Product product = loader.load(actor, productId);
        product.discontinue();
        return productRepository.save(product);
    }
}