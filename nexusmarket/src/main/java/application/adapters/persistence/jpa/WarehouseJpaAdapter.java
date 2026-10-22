package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaWarehouseRepository;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final SpringDataJpaWarehouseRepository repository;

    public WarehouseJpaAdapter(SpringDataJpaWarehouseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseJpaEntity saved = repository.save(WarehouseJpaMapper.toEntity(warehouse));
        if (warehouse.getId() == null) {
            warehouse.assignId(saved.getId());
        }
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(WarehouseJpaMapper::toDomain);
    }

    @Override
    public List<Warehouse> findBySellerId(Long sellerId) {
        return repository.findBySellerId(sellerId).stream().map(WarehouseJpaMapper::toDomain).toList();
    }

    @Override
    public List<Warehouse> findAll() {
        return repository.findAll().stream().map(WarehouseJpaMapper::toDomain).toList();
    }
}