package namoragroup.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import namoragroup.model.Admin;

@Service
public class JwtService {
    private static final String ALGORITHM = "HmacSHA256";
    private static final String HEADER = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long accessTokenSeconds;
    private final long refreshTokenSeconds;

    public JwtService(ObjectMapper mapper,
                      @Value("${app.jwt.secret}") String secretBase64,
                      @Value("${app.jwt.access-token-minutes:15}") long accessTokenMinutes,
                      @Value("${app.jwt.refresh-token-days:7}") long refreshTokenDays) {
        this.mapper = mapper;
        try {
            this.secret = Base64.getDecoder().decode(secretBase64);
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("JWT_SECRET doit être une clé encodée en Base64.", error);
        }
        if (this.secret.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET doit contenir au moins 32 octets aléatoires.");
        }
        this.accessTokenSeconds = accessTokenMinutes * 60;
        this.refreshTokenSeconds = refreshTokenDays * 24 * 60 * 60;
    }

    public String creerAccessToken(Admin admin) {
        return creer(admin, "access", null, accessTokenSeconds);
    }

    public String creerRefreshToken(Admin admin, String identifiant) {
        return creer(admin, "refresh", identifiant, refreshTokenSeconds);
    }

    private String creer(Admin admin, String type, String identifiant, long dureeSecondes) {
        long maintenant = Instant.now().getEpochSecond();
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", admin.getId().toString());
        claims.put("email", admin.getEmail());
        claims.put("role", admin.getRole());
        claims.put("type", type);
        claims.put("iat", maintenant);
        claims.put("exp", maintenant + dureeSecondes);
        if (identifiant != null) claims.put("jti", identifiant);
        try {
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(claims));
            String contenu = HEADER + "." + payload;
            return contenu + "." + signer(contenu);
        } catch (Exception error) {
            throw new IllegalStateException("Impossible de créer le jeton JWT.", error);
        }
    }

    public Claims verifier(String token) {
        try {
            String[] morceaux = token.split("\\.");
            if (morceaux.length != 3) throw new IllegalArgumentException("JWT invalide.");
            JsonNode entete = mapper.readTree(Base64.getUrlDecoder().decode(morceaux[0]));
            if (!"HS256".equals(entete.path("alg").asText())) throw new IllegalArgumentException("Algorithme JWT invalide.");
            String contenu = morceaux[0] + "." + morceaux[1];
            byte[] signatureRecue = Base64.getUrlDecoder().decode(morceaux[2]);
            byte[] signatureAttendue = Base64.getUrlDecoder().decode(signer(contenu));
            if (!MessageDigest.isEqual(signatureRecue, signatureAttendue)) throw new IllegalArgumentException("Signature JWT invalide.");

            JsonNode payload = mapper.readTree(Base64.getUrlDecoder().decode(morceaux[1]));
            long expiration = payload.path("exp").asLong(0);
            if (expiration <= Instant.now().getEpochSecond()) throw new IllegalArgumentException("JWT expiré.");
            String sujet = payload.path("sub").asText();
            String type = payload.path("type").asText();
            if (sujet.isBlank() || type.isBlank()) throw new IllegalArgumentException("Claims JWT manquants.");
            return new Claims(Long.parseLong(sujet), payload.path("email").asText(),
                    payload.path("role").asText(), type, payload.path("jti").asText(null), expiration);
        } catch (Exception error) {
            throw new InvalidJwtException("Jeton invalide ou expiré.", error);
        }
    }

    private String signer(String contenu) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secret, ALGORITHM));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(contenu.getBytes(StandardCharsets.UTF_8)));
    }

    public long getAccessTokenSeconds() { return accessTokenSeconds; }
    public long getRefreshTokenSeconds() { return refreshTokenSeconds; }

    public record Claims(long adminId, String email, String role, String type, String identifiant, long expiration) {}

    public static class InvalidJwtException extends RuntimeException {
        public InvalidJwtException(String message, Throwable cause) { super(message, cause); }
    }
}
