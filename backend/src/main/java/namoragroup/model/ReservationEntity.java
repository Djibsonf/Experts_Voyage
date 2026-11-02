package namoragroup.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import namoragroup.model.enums.StatutReservation;

@Entity @Table(name = "reservations", uniqueConstraints = @UniqueConstraint(columnNames = "reservation_code"))
public class ReservationEntity {
    @Id @Column(name = "reservation_id", length = 36) String id;
    @Column(name = "reservation_code", nullable = false, length = 24) String code;
    @Column(name = "customer_id", nullable = false) Long clientId;
    @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Column(name = "seat_number", nullable = false) Integer numeroSiege;
    @Column(name = "origin_stop_id", nullable = false) Long etapeDepartId;
    @Column(name = "destination_stop_id", nullable = false) Long etapeArriveeId;
    @Column(name = "departure_point_id", nullable = false) Long lieuDepartId;
    @Column(name = "amount_xof", nullable = false, precision = 12, scale = 0) BigDecimal montant;
    @Column(nullable = false, length = 3) String currency = "XOF";
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 24) StatutReservation statut = StatutReservation.EN_ATTENTE_PAIEMENT;
    @Column(name = "hold_expires_at", nullable = false) LocalDateTime expireLe;
    @Column(name = "paid_at") LocalDateTime payeLe;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    @Column(name = "updated_at", insertable = false, updatable = false) LocalDateTime modifieLe;
    protected ReservationEntity() {}
}
