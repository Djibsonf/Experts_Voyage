package namoragroup.dto.response;

import java.time.LocalDateTime;
import namoragroup.model.enums.StatutReservation;

public record PendingReservationResponse(
        String reservationId,
        String reservationCode,
        StatutReservation statut,
        int seatNumber,
        long destinationStopId,
        long amountXof,
        String wavePaymentUrl,
        LocalDateTime holdExpiresAt) {}
