package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.persistence.jpa.mappers.ReturnJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaRefundRepository;
import application.domain.models.Refund;
import application.domain.ports.out.RefundRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RefundJpaAdapter implements RefundRepositoryPort {

    private final SpringDataJpaRefundRepository repository;

    public RefundJpaAdapter(SpringDataJpaRefundRepository repository) {
        this.repository = repository;
    }

    @Override
    public Refund save(Refund refund) {
        RefundJpaEntity saved = repository.save(ReturnJpaMapper.toEntity(refund));
        if (refund.getId() == null) {
            refund.assignId(saved.getId());
        }
        return refund;
    }

    @Override
    public Optional<Refund> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(ReturnJpaMapper::toDomain);
    }

    @Override
    public List<Refund> findAll() {
        return repository.findAll().stream().map(ReturnJpaMapper::toDomain).toList();
    }
}