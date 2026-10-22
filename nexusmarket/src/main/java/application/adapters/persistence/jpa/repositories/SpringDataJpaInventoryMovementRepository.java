package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaInventoryMovementRepository
        extends JpaRepository<InventoryMovementJpaEntity, Long> {
}