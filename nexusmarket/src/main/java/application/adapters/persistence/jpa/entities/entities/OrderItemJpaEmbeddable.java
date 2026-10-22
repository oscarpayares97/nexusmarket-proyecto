package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Una línea de pedido o de factura (no es tabla propia: vive dentro de una tabla hija). */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class OrderItemJpaEmbeddable {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    /** Nombre del enum ProductType. */
    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /** Null en productos digitales. */
    @Column(name = "warehouse_id")
    private Long warehouseId;
}