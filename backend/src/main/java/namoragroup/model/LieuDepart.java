package namoragroup.model;

import jakarta.persistence.*;

@Entity @Table(name = "lieux_depart", uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "name"}))
public class LieuDepart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "departure_point_id") Long id;
    @Column(name = "city_id", nullable = false) Long villeId;
    @Column(nullable = false, length = 120) String name;
    @Column(nullable = false) boolean active = true;
    protected LieuDepart() {}
}
