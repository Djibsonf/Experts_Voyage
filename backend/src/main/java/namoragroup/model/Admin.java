package namoragroup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateurs_admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_user_id")
    private Long id;

    @Column(name = "nom_complet", nullable = false, length = 160)
    private String nomComplet;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String motDePasseHash;

    @Column(nullable = false, length = 24)
    private String role = "ADMIN";

    @Column(nullable = false)
    private boolean active = true;

    protected Admin() {}

    public Admin(String nomComplet, String email, String motDePasseHash, String role, boolean active) {
        this.nomComplet = nomComplet;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public String getEmail() { return email; }
    public String getMotDePasseHash() { return motDePasseHash; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public void changerMotDePasse(String nouveauHash) { this.motDePasseHash = nouveauHash; }
}
