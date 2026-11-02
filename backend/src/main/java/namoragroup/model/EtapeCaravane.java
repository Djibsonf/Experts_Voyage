package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "etapes_caravane", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"caravan_id", "stop_order"}),
        @UniqueConstraint(columnNames = {"caravan_id", "city_id"})})
public class EtapeCaravane {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "stop_id") Long id;
    @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Column(name = "city_id", nullable = false) Long villeId;
    @Column(name = "stop_order", nullable = false) Integer ordre;
    @Column(name = "arrival_at") LocalDateTime arriveeLe;
    protected EtapeCaravane() {}
}
