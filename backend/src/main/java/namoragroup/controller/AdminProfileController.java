package namoragroup.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import namoragroup.dto.request.ChangeAdminPasswordRequest;
import namoragroup.dto.response.AdminCaravanDtos;
import namoragroup.exception.ApiException;
import namoragroup.model.Admin;
import namoragroup.repository.AdminRepository;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/admin/profil")
public class AdminProfileController {
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;

    public AdminProfileController(AdminRepository admins, PasswordEncoder passwordEncoder) {
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public AdminCaravanDtos.AdminProfile profile(Principal principal) {
        Admin admin = findAdmin(principal.getName());
        return new AdminCaravanDtos.AdminProfile(admin.getNomComplet(), admin.getEmail(), admin.getRole());
    }

    @PutMapping("/mot-de-passe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void changePassword(@Valid @RequestBody ChangeAdminPasswordRequest request, Principal principal) {
        Admin admin = findAdmin(principal.getName());
        if (!passwordEncoder.matches(request.ancienMotDePasse(), admin.getMotDePasseHash())) {
            throw new ApiException(UNAUTHORIZED, "MOT_DE_PASSE_INCORRECT", "Le mot de passe actuel est incorrect.");
        }
        admin.changerMotDePasse(passwordEncoder.encode(request.nouveauMotDePasse()));
        admins.save(admin);
    }

    private Admin findAdmin(String email) {
        return admins.findByEmail(email)
                .filter(Admin::isActive)
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "ADMIN_INACTIF", "Compte administrateur introuvable ou désactivé."));
    }
}
