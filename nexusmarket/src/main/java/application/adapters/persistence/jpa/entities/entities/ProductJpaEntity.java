package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** Nombre del enum ProductType. */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /** Money.amount */
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /** Nombre del enum ProductStatus. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_variants", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "variant", length = 100)
    private List<String> variants = new ArrayList<>();
}