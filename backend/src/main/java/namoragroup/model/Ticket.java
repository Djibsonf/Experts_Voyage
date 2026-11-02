package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "tickets", uniqueConstraints = {
        @UniqueConstraint(columnNames = "reservation_id"), @UniqueConstraint(columnNames = "ticket_code")})
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "ticket_id") Long id;
    @Column(name = "reservation_id", nullable = false, length = 36) String reservationId;
    @Column(name = "ticket_code", nullable = false, length = 32) String code;
    @Column(name = "pdf_path", length = 500) String cheminPdf;
    @Column(name = "png_path", length = 500) String cheminPng;
    @Column(name = "generated_at") LocalDateTime genereLe;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    protected Ticket() {}
}
