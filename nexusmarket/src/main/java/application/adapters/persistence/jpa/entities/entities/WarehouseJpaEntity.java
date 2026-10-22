package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    /** Nombre del enum WarehouseType. */
    @Column(name = "type", nullable = false, length = 40)
    private String type;

    /** Null si la bodega es del Marketplace. */
    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "active", nullable = false)
    private boolean active;
}