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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "buyer_name", nullable = false, length = 120)
    private String buyerName;

    @Column(name = "buyer_document", nullable = false, length = 20)
    private String buyerDocument;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "invoice_lines", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<OrderItemJpaEmbeddable> lines = new ArrayList<>();
}