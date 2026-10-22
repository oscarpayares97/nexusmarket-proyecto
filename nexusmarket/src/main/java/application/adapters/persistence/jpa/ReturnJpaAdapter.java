package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import application.adapters.persistence.jpa.mappers.ReturnJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaReturnRepository;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ReturnJpaAdapter implements ReturnRepositoryPort {

    private final SpringDataJpaReturnRepository repository;

    public ReturnJpaAdapter(SpringDataJpaReturnRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReturnRequest save(ReturnRequest returnRequest) {
        ReturnRequestJpaEntity saved = repository.save(ReturnJpaMapper.toEntity(returnRequest));
        if (returnRequest.getId() == null) {
            returnRequest.assignId(saved.getId());
        }
        return returnRequest;
    }

    @Override
    public Optional<ReturnRequest> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(ReturnJpaMapper::toDomain);
    }

    @Override
    public List<ReturnRequest> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).stream().map(ReturnJpaMapper::toDomain).toList();
    }

    @Override
    public List<ReturnRequest> findByBuyerId(Long buyerId) {
        return repository.findByBuyerId(buyerId).stream().map(ReturnJpaMapper::toDomain).toList();
    }

    @Override
    public List<ReturnRequest> findAll() {
        return repository.findAll().stream().map(ReturnJpaMapper::toDomain).toList();
    }
}