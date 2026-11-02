package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import namoragroup.model.enums.StatutCaravane;

@Entity @Table(name = "caravanes")
public class Caravane {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "caravan_id") Long id;
    @Column(name = "vehicle_id", nullable = false) Long busId;
    @Column(name = "caravan_code", nullable = false, unique = true, length = 32) String code;
    @Column(name = "name", nullable = false, length = 100) String nom;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20) StatutCaravane statut = StatutCaravane.BROUILLON;
    @Column(name = "departure_at", nullable = false) LocalDateTime departLe;
    @Column(length = 500) String notes;
    @Column(name = "archived", nullable = false) boolean archivee;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    @Column(name = "updated_at", insertable = false, updatable = false) LocalDateTime modifieLe;
    protected Caravane() {}
}
