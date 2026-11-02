package namoragroup.dto.response;

import java.time.LocalDateTime;
import namoragroup.model.enums.StatutReservation;

public record ReservationCreatedResponse(
        String reservationId,
        String reservationCode,
        StatutReservation statut,
        int seatNumber,
        long amountXof,
        String wavePaymentUrl,
        LocalDateTime holdExpiresAt) {}
