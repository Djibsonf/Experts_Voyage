package namoragroup.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import namoragroup.config.JwtService;
import namoragroup.dto.request.AdminLoginRequest;
import namoragroup.dto.response.TokenPairResponse;
import namoragroup.exception.ApiException;
import namoragroup.model.Admin;
import namoragroup.model.JetonRafraichissement;
import namoragroup.repository.AdminRepository;
import namoragroup.repository.JetonRafraichissementRepository;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
    private final AdminRepository admins;
    private final JetonRafraichissementRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdminRepository admins, JetonRafraichissementRepository refreshTokens,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.admins = admins;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenPairResponse connecter(AdminLoginRequest request) {
        Admin admin = admins.findByEmail(request.email().trim().toLowerCase())
                .filter(Admin::isActive)
                .filter(candidate -> passwordEncoder.matches(request.motDePasse(), candidate.getMotDePasseHash()))
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "IDENTIFIANTS_INVALIDES", "Adresse e-mail ou mot de passe incorrect."));
        return creerPaire(admin);
    }

    @Transactional
    public TokenPairResponse rafraichir(String refreshToken) {
        JwtService.Claims claims = verifierRefresh(refreshToken);
        var savedToken = refreshTokens.findByIdentifiantJetonAndAdminIdAndRevokedFalse(
                        claims.identifiant(), claims.adminId())
                .filter(token -> token.getExpireLe().isAfter(LocalDateTime.now(ZoneOffset.UTC)))
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "REFRESH_TOKEN_INVALIDE", "Le jeton de renouvellement est invalide ou déjà utilisé."));
        Admin admin = admins.findById(claims.adminId())
                .filter(Admin::isActive)
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "ADMIN_INACTIF", "Ce compte administrateur est désactivé."));
        savedToken.revoquer();
        refreshTokens.save(savedToken);
        return creerPaire(admin);
    }

    @Transactional
    public void deconnecter(String refreshToken) {
        JwtService.Claims claims = verifierRefresh(refreshToken);
        refreshTokens.findByIdentifiantJetonAndAdminIdAndRevokedFalse(claims.identifiant(), claims.adminId())
                .ifPresent(token -> {
                    token.revoquer();
                    refreshTokens.save(token);
                });
    }

    private TokenPairResponse creerPaire(Admin admin) {
        String jti = UUID.randomUUID().toString();
        LocalDateTime expireLe = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(jwtService.getRefreshTokenSeconds());
        String refreshToken = jwtService.creerRefreshToken(admin, jti);
        refreshTokens.save(new JetonRafraichissement(admin.getId(), jti, expireLe));
        return new TokenPairResponse(jwtService.creerAccessToken(admin), refreshToken, "Bearer",
                jwtService.getAccessTokenSeconds(), jwtService.getRefreshTokenSeconds());
    }

    private JwtService.Claims verifierRefresh(String token) {
        try {
            JwtService.Claims claims = jwtService.verifier(token);
            if (!"refresh".equals(claims.type()) || claims.identifiant() == null || claims.identifiant().isBlank()) {
                throw new IllegalArgumentException("Ce n’est pas un jeton de renouvellement.");
            }
            return claims;
        } catch (JwtService.InvalidJwtException | IllegalArgumentException error) {
            throw new ApiException(UNAUTHORIZED, "REFRESH_TOKEN_INVALIDE", "Le jeton de renouvellement est invalide ou expiré.");
        }
    }
}
