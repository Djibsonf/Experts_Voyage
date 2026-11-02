package namoragroup.model;

import java.time.LocalDateTime;
import namoragroup.model.enums.StatutReservation;

public record Reservation(
        String id,
        String code,
        long customerId,
        long caravanId,
        int seatNumber,
        long originStopId,
        long destinationStopId,
        long departurePointId,
        long amountXof,
        StatutReservation statut,
        LocalDateTime holdExpiresAt) {}
