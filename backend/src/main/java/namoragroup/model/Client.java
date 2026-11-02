package namoragroup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "clients")
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "customer_id") Long id;
    @Column(name = "first_name", nullable = false, length = 100) String prenom;
    @Column(name = "last_name", nullable = false, length = 100) String nom;
    @Column(name = "document_type", nullable = false, length = 24) String typeDocument;
    @Column(name = "document_number", nullable = false, length = 80) String numeroDocument;
    @Column(name = "created_at", insertable = false, updatable = false) LocalDateTime creeLe;
    protected Client() {}
}
