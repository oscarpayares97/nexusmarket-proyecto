package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;

public class AddProductVariantService {

    private final OwnedProductLoader loader;
    private final ProductRepositoryPort productRepository;

    public AddProductVariantService(OwnedProductLoader loader, ProductRepositoryPort productRepository) {
        this.loader = loader;
        this.productRepository = productRepository;
    }

    public Product add(User actor, Long productId, String variant) {
        Product product = loader.load(actor, productId);
        product.addVariant(variant);
        return productRepository.save(product);
    }
}