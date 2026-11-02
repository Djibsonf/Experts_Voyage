package namoragroup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "jetons_rafraichissement")
public class JetonRafraichissement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "token_id_jwt", nullable = false, unique = true, length = 36)
    private String identifiantJeton;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expireLe;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime creeLe;

    protected JetonRafraichissement() {}

    public JetonRafraichissement(Long adminId, String identifiantJeton, LocalDateTime expireLe) {
        this.adminId = adminId;
        this.identifiantJeton = identifiantJeton;
        this.expireLe = expireLe;
        this.revoked = false;
        this.creeLe = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAdminId() { return adminId; }
    public String getIdentifiantJeton() { return identifiantJeton; }
    public LocalDateTime getExpireLe() { return expireLe; }
    public boolean isRevoked() { return revoked; }
    public void revoquer() { this.revoked = true; }
}
