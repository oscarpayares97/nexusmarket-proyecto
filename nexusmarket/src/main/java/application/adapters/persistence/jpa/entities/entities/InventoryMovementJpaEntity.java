package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Nombre del enum MovementType. */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}