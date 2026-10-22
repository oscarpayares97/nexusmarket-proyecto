package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
public class SellerJpaEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "trade_name", nullable = false, length = 120)
    private String tradeName;
}