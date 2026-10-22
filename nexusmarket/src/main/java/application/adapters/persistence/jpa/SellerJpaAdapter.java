package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaSellerRepository;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SellerJpaAdapter implements SellerRepositoryPort {

    private final SpringDataJpaSellerRepository repository;

    public SellerJpaAdapter(SpringDataJpaSellerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Seller save(Seller seller) {
        repository.save(SellerJpaMapper.toEntity(seller));
        return seller;
    }

    @Override
    public Optional<Seller> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return repository.findById(userId).map(SellerJpaMapper::toDomain);
    }
}