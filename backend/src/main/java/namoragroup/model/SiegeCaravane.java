package namoragroup.model;

import jakarta.persistence.*;

@Entity @IdClass(SiegeCaravaneId.class) @Table(name = "sieges_caravane")
public class SiegeCaravane {
    @Id @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Id @Column(name = "seat_number", nullable = false) Integer numero;
    @Column(name = "seat_row") Integer rangee;
    @Column(name = "position_in_row") Integer positionDansRangee;
    @Column(length = 12) String side;
    @Column(name = "is_back_row", nullable = false) boolean rangeeDuFond;
    protected SiegeCaravane() {}
}
