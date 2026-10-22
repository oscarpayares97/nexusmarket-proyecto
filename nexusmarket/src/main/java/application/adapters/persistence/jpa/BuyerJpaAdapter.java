package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaBuyerRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BuyerJpaAdapter implements BuyerRepositoryPort {

    private final SpringDataJpaBuyerRepository repository;

    public BuyerJpaAdapter(SpringDataJpaBuyerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Buyer save(Buyer buyer) {
        repository.save(BuyerJpaMapper.toEntity(buyer));
        return buyer;
    }

    @Override
    public Optional<Buyer> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return repository.findById(userId).map(BuyerJpaMapper::toDomain);
    }
}