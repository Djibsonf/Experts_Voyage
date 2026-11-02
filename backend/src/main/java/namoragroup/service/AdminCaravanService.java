package namoragroup.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import namoragroup.dto.request.CreateCaravanRequest;
import namoragroup.dto.request.UpdateCaravanRequest;
import namoragroup.dto.response.AdminCaravanDtos;
import namoragroup.exception.ApiException;
import namoragroup.repository.AdminCaravanRepository;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AdminCaravanService {
    private final AdminCaravanRepository caravans;

    public AdminCaravanService(AdminCaravanRepository caravans) {
        this.caravans = caravans;
    }

    public List<AdminCaravanDtos.CaravanItem> current() { return caravans.findCurrent(); }
    public List<AdminCaravanDtos.CaravanItem> history() { return caravans.findHistory(); }

    public AdminCaravanDtos.CaravanItem get(long id) {
        AdminCaravanDtos.CaravanItem caravan = caravans.findOne(id);
        if (caravan == null) throw new ApiException(NOT_FOUND, "CARAVANE_INTROUVABLE", "Cette caravane n’existe pas.");
        return caravan;
    }

    @Transactional
    public long create(CreateCaravanRequest request) {
        boolean dakar = "Dakar".equalsIgnoreCase(request.villeDepart().trim());
        boolean ziguinchor = "Ziguinchor".equalsIgnoreCase(request.villeDepart().trim());
        if (!dakar && !ziguinchor) {
            throw new ApiException(BAD_REQUEST, "VILLE_DEPART_INVALIDE", "Le départ doit être Dakar ou Ziguinchor.");
        }
        if (!request.departLe().isAfter(LocalDateTime.now())) {
            throw new ApiException(BAD_REQUEST, "DATE_INVALIDE", "La date de départ doit être dans le futur.");
        }
        verifierLienWave(request.lienWaveDepartDestination());
        boolean tarifVersKaolack = request.prixDepartKaolack() != null;
        boolean lienVersKaolack = request.lienWaveDepartKaolack() != null && !request.lienWaveDepartKaolack().isBlank();
        if (tarifVersKaolack != lienVersKaolack) {
            throw new ApiException(BAD_REQUEST, "TARIF_KAOLACK_INCOMPLET", "Pour proposer le trajet vers Kaolack, renseignez son prix et son lien Wave.");
        }
        if (tarifVersKaolack) verifierLienWave(request.lienWaveDepartKaolack());
        boolean tarifDepuisKaolack = request.prixKaolackDestination() != null;
        boolean lienDepuisKaolack = request.lienWaveKaolackDestination() != null && !request.lienWaveKaolackDestination().isBlank();
        if (tarifDepuisKaolack != lienDepuisKaolack) {
            throw new ApiException(BAD_REQUEST, "TARIF_KAOLACK_INCOMPLET", "Pour proposer le trajet depuis Kaolack, renseignez son prix et son lien Wave.");
        }
        if (tarifDepuisKaolack) verifierLienWave(request.lienWaveKaolackDestination());
        CreateCaravanRequest normalized = new CreateCaravanRequest(
                request.nom().trim(), dakar ? "Dakar" : "Ziguinchor", request.departLe(), request.capacite(), request.prixDepartKaolack(),
                lienVersKaolack ? request.lienWaveDepartKaolack().trim() : null, request.prixDepartDestination(),
                request.lienWaveDepartDestination(), request.prixKaolackDestination(),
                lienDepuisKaolack ? request.lienWaveKaolackDestination().trim() : null);
        return caravans.create(normalized);
    }

    @Transactional
    public void update(long id, UpdateCaravanRequest request) {
        verifierExiste(id);
        caravans.update(id, request);
    }

    @Transactional
    public void archive(long id, boolean archivee) {
        verifierExiste(id);
        caravans.setArchived(id, archivee);
    }

    public List<AdminCaravanDtos.Passenger> passengers(long id) {
        verifierExiste(id);
        return caravans.findPassengers(id);
    }

    public boolean exists(long id) { return caravans.exists(id); }
    public long countCurrent() { return caravans.countCurrent(); }
    public long countPassengers() { return caravans.countPassengers(); }

    @Transactional
    public void deleteUnpaidReservation(String reservationId) {
        try {
            if (!caravans.deleteUnpaidReservation(reservationId)) {
                throw new ApiException(NOT_FOUND, "RESERVATION_INTROUVABLE", "Cette réservation n’existe pas.");
            }
        } catch (IllegalStateException error) {
            throw new ApiException(CONFLICT, "RESERVATION_DEJA_PAYEE", error.getMessage());
        }
    }

    private void verifierExiste(long id) {
        if (!caravans.exists(id)) throw new ApiException(NOT_FOUND, "CARAVANE_INTROUVABLE", "Cette caravane n’existe pas.");
    }

    private void verifierLienWave(String url) {
        try {
            if (url == null || url.isBlank()) throw new IllegalArgumentException();
            URI uri = URI.create(url.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) throw new IllegalArgumentException();
        } catch (RuntimeException error) {
            throw new ApiException(BAD_REQUEST, "LIEN_WAVE_INVALIDE", "Saisissez un lien Wave commençant par https://.");
        }
    }
}
