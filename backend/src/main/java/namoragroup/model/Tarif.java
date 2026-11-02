package namoragroup.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "tarifs", uniqueConstraints = @UniqueConstraint(columnNames = {"caravan_id", "origin_stop_id", "destination_stop_id"}))
public class Tarif {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "fare_id") Long id;
    @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Column(name = "origin_stop_id", nullable = false) Long etapeDepartId;
    @Column(name = "destination_stop_id", nullable = false) Long etapeArriveeId;
    @Column(name = "amount_xof", nullable = false, precision = 12, scale = 0) BigDecimal montant;
    @Column(name = "wave_payment_url", length = 500) String lienPaiementWave;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    protected Tarif() {}
}
