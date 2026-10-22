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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
public class ReturnRequestJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    /** Nombre del enum ReturnStatus. */
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "return_items", joinColumns = @JoinColumn(name = "return_request_id"))
    private List<ReturnItemJpaEmbeddable> items = new ArrayList<>();
}