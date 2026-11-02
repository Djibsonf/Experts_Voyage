package namoragroup.model;

import jakarta.persistence.*;

@Entity @Table(name = "villes")
public class Ville {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "city_id") Long id;
    @Column(name = "city_code", nullable = false, unique = true, length = 12) String code;
    @Column(nullable = false, unique = true, length = 80) String name;
    @Column(nullable = false) boolean active = true;
    protected Ville() {}
}
