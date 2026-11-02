package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import namoragroup.model.enums.StatutSiege;

@Entity @IdClass(SiegeReserveId.class) @Table(name = "sieges_reserves", uniqueConstraints = {
        @UniqueConstraint(columnNames = "reservation_id")})
public class SiegeReserve {
    @Id @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Id @Column(name = "seat_number", nullable = false) Integer numeroSiege;
    @Column(name = "reservation_id", nullable = false, length = 36) String reservationId;
    @Enumerated(EnumType.STRING) @Column(name = "claim_status", nullable = false, length = 16) StatutSiege statut;
    @Column(name = "expires_at") LocalDateTime expireLe;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    protected SiegeReserve() {}
}
