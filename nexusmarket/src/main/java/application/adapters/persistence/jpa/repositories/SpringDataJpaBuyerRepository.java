package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaBuyerRepository extends JpaRepository<BuyerJpaEntity, Long> {
}