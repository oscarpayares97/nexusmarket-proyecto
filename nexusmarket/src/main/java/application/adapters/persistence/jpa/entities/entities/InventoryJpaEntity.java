package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Un registro de inventario por (producto, bodega). */
@Entity
@Table(name = "inventories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}))
@Getter
@Setter
@NoArgsConstructor
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "available", nullable = false)
    private int available;

    @Column(name = "reserved", nullable = false)
    private int reserved;

    /** Nombre del enum InventoryStatus. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}