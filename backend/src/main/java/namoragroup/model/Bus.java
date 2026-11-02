package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "bus")
public class Bus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "vehicle_id") Long id;
    @Column(nullable = false, length = 100) String label;
    @Column(name = "registration_number", unique = true, length = 40) String immatriculation;
    @Column(name = "seat_capacity", nullable = false) Integer capaciteSieges;
    @Column(name = "layout_confirmed", nullable = false) boolean dispositionConfirmee;
    @Column(nullable = false) boolean active = true;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    protected Bus() {}
}
