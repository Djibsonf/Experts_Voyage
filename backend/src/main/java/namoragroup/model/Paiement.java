package namoragroup.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import namoragroup.model.enums.StatutPaiement;

@Entity @Table(name = "paiements")
public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "payment_attempt_id") Long id;
    @Column(name = "reservation_id", nullable = false, length = 36) String reservationId;
    @Column(nullable = false, length = 20) String provider = "WAVE";
    @Column(name = "checkout_session_id", unique = true, length = 40) String sessionPaiement;
    @Column(name = "transaction_id", unique = true, length = 80) String transactionId;
    @Column(name = "payment_url", length = 500) String lienPaiement;
    @Column(name = "amount_xof", nullable = false, precision = 12, scale = 0) BigDecimal montant;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20) StatutPaiement statut = StatutPaiement.INITIE;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    @Column(name = "updated_at", insertable = false, updatable = false) LocalDateTime modifieLe;
    protected Paiement() {}
}
