package namoragroup.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import namoragroup.dto.request.CreateReservationRequest;
import namoragroup.dto.request.RecoverReservationRequest;
import namoragroup.dto.response.ReservationCreatedResponse;
import namoragroup.dto.response.PendingReservationResponse;
import namoragroup.exception.ApiException;
import namoragroup.model.TarifVoyage;
import namoragroup.model.enums.StatutReservation;
import namoragroup.repository.ReservationRepository;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
public class ReservationService {
    private final ReservationRepository reservations;
    private final int holdMinutes;

    public ReservationService(ReservationRepository reservations, @Value("${app.reservation.hold-minutes}") int holdMinutes) {
        this.reservations = reservations;
        this.holdMinutes = holdMinutes;
    }

    @Transactional
    public ReservationCreatedResponse reserve(CreateReservationRequest request) {
        TarifVoyage tarif = reservations.findFare(request);
        if (tarif == null || tarif.montantXof() <= 0 || tarif.lienPaiementWave() == null || tarif.lienPaiementWave().isBlank()) {
            throw new ApiException(UNPROCESSABLE_ENTITY, "TRIP_UNAVAILABLE", "Ce trajet n’est pas ouvert à la réservation ou son tarif n’est pas configuré.");
        }
        if (!reservations.seatExists(request)) {
            throw new ApiException(NOT_FOUND, "SEAT_NOT_FOUND", "Ce numéro de siège n’existe pas dans cette caravane.");
        }
        if (!reservations.departurePointAvailable(request)) {
            throw new ApiException(UNPROCESSABLE_ENTITY, "DEPARTURE_POINT_UNAVAILABLE", "Ce lieu de départ n’est pas proposé pour le trajet sélectionné.");
        }

        String reservationId = UUID.randomUUID().toString();
        String reservationCode = "EV-" + reservationId.replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiresAt = now.plusMinutes(holdMinutes);
        long customerId = reservations.createCustomer(request);

        reservations.expireOldClaim(request.caravanId(), request.seatNumber());
        reservations.createReservation(reservationId, reservationCode, customerId, request, tarif.montantXof(), expiresAt, now);
        reservations.claimSeat(request.caravanId(), request.seatNumber(), reservationId, expiresAt);

        return new ReservationCreatedResponse(
                reservationId, reservationCode, StatutReservation.EN_ATTENTE_PAIEMENT,
                request.seatNumber(), tarif.montantXof(), tarif.lienPaiementWave(), expiresAt);
    }

    public PendingReservationResponse findPending(String reservationId) {
        PendingReservationResponse pending = reservations.findPending(reservationId);
        if (pending == null) throw new ApiException(NOT_FOUND, "RESERVATION_NOT_FOUND", "Cette réservation n’est plus en attente de paiement.");
        return pending;
    }

    public PendingReservationResponse recover(RecoverReservationRequest request) {
        PendingReservationResponse pending = reservations.findPendingForCustomer(request);
        if (pending == null) throw new ApiException(NOT_FOUND, "RESERVATION_NOT_FOUND", "Aucune réservation non payée active ne correspond à ces informations.");
        return pending;
    }

    @Transactional
    public void cancelPending(String reservationId) {
        if (!reservations.cancelPending(reservationId)) {
            throw new ApiException(CONFLICT, "RESERVATION_NOT_CANCELLABLE", "Cette réservation a expiré ou son paiement a déjà été traité.");
        }
    }
}
