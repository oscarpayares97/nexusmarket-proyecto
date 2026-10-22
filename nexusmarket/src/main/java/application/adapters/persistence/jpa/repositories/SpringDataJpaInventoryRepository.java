package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaInventoryRepository extends JpaRepository<InventoryJpaEntity, Long> {

    Optional<InventoryJpaEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<InventoryJpaEntity> findByProductId(Long productId);
}