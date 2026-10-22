package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaProductRepository;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductJpaAdapter implements ProductRepositoryPort {

    private final SpringDataJpaProductRepository repository;

    public ProductJpaAdapter(SpringDataJpaProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = repository.save(ProductJpaMapper.toEntity(product));
        if (product.getId() == null) {
            product.assignId(saved.getId());
        }
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(ProductJpaMapper::toDomain);
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {
        return repository.findBySellerId(sellerId).stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public List<Product> findPublished() {
        return repository.findByStatus(ProductStatus.PUBLISHED.name()).stream()
                .map(ProductJpaMapper::toDomain).toList();
    }
}