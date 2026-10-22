package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
public class BuyerJpaEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "primary_address", nullable = false, length = 200)
    private String primaryAddress;

    @Column(name = "commercial_status", nullable = false, length = 40)
    private String commercialStatus;

    /** Direcciones adicionales: tabla hija "buyer_addresses". */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "buyer_addresses", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "address", length = 200)
    private List<String> additionalAddresses = new ArrayList<>();
}