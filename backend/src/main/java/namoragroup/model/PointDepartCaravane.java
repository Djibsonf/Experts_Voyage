package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @IdClass(PointDepartCaravaneId.class) @Table(name = "points_depart_caravane")
public class PointDepartCaravane {
    @Id @Column(name = "caravan_id", nullable = false) Long caravaneId;
    @Column(name = "stop_id", nullable = false) Long etapeId;
    @Id @Column(name = "departure_point_id", nullable = false) Long lieuDepartId;
    @Column(name = "pickup_order", nullable = false) Integer ordre = 1;
    @Column(name = "departure_at") LocalDateTime departLe;
    protected PointDepartCaravane() {}
}
