package namoragroup.model;

import jakarta.persistence.*;

@Entity @IdClass(SiegeBusId.class) @Table(name = "sieges_bus")
public class SiegeBus {
    @Id @Column(name = "vehicle_id", nullable = false) Long busId;
    @Id @Column(name = "seat_number", nullable = false) Integer numero;
    @Column(name = "seat_row") Integer rangee;
    @Column(name = "position_in_row") Integer positionDansRangee;
    @Column(length = 12) String side;
    @Column(name = "is_back_row", nullable = false) boolean rangeeDuFond;
    protected SiegeBus() {}
}
