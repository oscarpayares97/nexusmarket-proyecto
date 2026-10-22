package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.adapters.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaShipmentRepository;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentJpaAdapter implements ShipmentRepositoryPort {

    private final SpringDataJpaShipmentRepository repository;

    public ShipmentJpaAdapter(SpringDataJpaShipmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentJpaEntity saved = repository.save(ShipmentJpaMapper.toEntity(shipment));
        if (shipment.getId() == null) {
            shipment.assignId(saved.getId());
        }
        return shipment;
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(ShipmentJpaMapper::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(ShipmentJpaMapper::toDomain);
    }

    @Override
    public List<Shipment> findAll() {
        return repository.findAll().stream().map(ShipmentJpaMapper::toDomain).toList();
    }
}