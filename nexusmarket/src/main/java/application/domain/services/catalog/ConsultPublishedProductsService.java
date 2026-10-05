package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.ports.in.ConsultPublishedProductsUseCase;
import application.domain.ports.out.ProductRepositoryPort;

import java.util.List;

public class ConsultPublishedProductsService implements ConsultPublishedProductsUseCase {

    private final ProductRepositoryPort productRepository;

    public ConsultPublishedProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> consult() {
        return productRepository.findPublished();
    }
}