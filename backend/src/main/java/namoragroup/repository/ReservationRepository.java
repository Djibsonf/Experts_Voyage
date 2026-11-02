package namoragroup.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import namoragroup.dto.request.CreateReservationRequest;
import namoragroup.dto.request.RecoverReservationRequest;
import namoragroup.model.TarifVoyage;
import namoragroup.dto.response.PendingReservationResponse;
import namoragroup.model.enums.StatutReservation;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbc;

    public ReservationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TarifVoyage findFare(CreateReservationRequest request) {
        try {
            return jdbc.queryForObject("""
                    SELECT fare.amount_xof, fare.wave_payment_url
                    FROM tarifs fare
                    JOIN caravanes caravan ON caravan.caravan_id = fare.caravan_id
                    JOIN etapes_caravane origin ON origin.stop_id = fare.origin_stop_id AND origin.caravan_id = fare.caravan_id
                    JOIN etapes_caravane destination ON destination.stop_id = fare.destination_stop_id AND destination.caravan_id = fare.caravan_id
                    WHERE fare.caravan_id = ? AND origin.stop_id = ? AND destination.stop_id = ?
                      AND origin.stop_order < destination.stop_order AND caravan.status = 'OUVERTE'
                      AND caravan.archived = FALSE
                      AND caravan.departure_at > UTC_TIMESTAMP()
                    """, (rs, row) -> new TarifVoyage(rs.getLong("amount_xof"), rs.getString("wave_payment_url")),
                    request.caravanId(), request.originStopId(), request.destinationStopId());
        } catch (EmptyResultDataAccessException error) {
            return null;
        }
    }

    public boolean seatExists(CreateReservationRequest request) {
        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM sieges_caravane WHERE caravan_id = ? AND seat_number = ?
                """, Integer.class, request.caravanId(), request.seatNumber());
        return count != null && count > 0;
    }

    public boolean departurePointAvailable(CreateReservationRequest request) {
        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM points_depart_caravane point
                JOIN etapes_caravane stop ON stop.stop_id = point.stop_id AND stop.caravan_id = point.caravan_id
                JOIN lieux_depart location ON location.departure_point_id = point.departure_point_id
                WHERE point.caravan_id = ? AND stop.stop_id = ? AND location.departure_point_id = ? AND location.active = TRUE
                """, Integer.class, request.caravanId(), request.originStopId(), request.departurePointId());
        return count != null && count > 0;
    }

    public long createCustomer(CreateReservationRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO clients (first_name, last_name, document_type, document_number)
                    VALUES (?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.firstName().trim());
            statement.setString(2, request.lastName().trim());
            statement.setString(3, request.documentType().name());
            statement.setString(4, request.documentNumber().trim());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("La fiche client n’a pas pu être créée.");
        return key.longValue();
    }

    public void expireOldClaim(long caravanId, int seatNumber) {
        jdbc.update("""
                UPDATE reservations reservation
                JOIN sieges_reserves claim ON claim.reservation_id = reservation.reservation_id
                SET reservation.status = 'EXPIREE'
                WHERE claim.caravan_id = ? AND claim.seat_number = ? AND claim.claim_status = 'RETENU'
                  AND claim.expires_at <= UTC_TIMESTAMP() AND reservation.status = 'EN_ATTENTE_PAIEMENT'
                """, caravanId, seatNumber);
        jdbc.update("""
                DELETE FROM sieges_reserves
                WHERE caravan_id = ? AND seat_number = ? AND claim_status = 'RETENU' AND expires_at <= UTC_TIMESTAMP()
                """, caravanId, seatNumber);
    }

    public void createReservation(String id, String code, long customerId, CreateReservationRequest request,
                                  long amountXof, LocalDateTime expiresAt, LocalDateTime createdAt) {
        jdbc.update("""
                INSERT INTO reservations
                  (reservation_id, reservation_code, customer_id, caravan_id, seat_number,
                   origin_stop_id, destination_stop_id, departure_point_id, amount_xof, currency,
                   status, hold_expires_at, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'XOF', 'EN_ATTENTE_PAIEMENT', ?, ?)
                """, id, code, customerId, request.caravanId(), request.seatNumber(),
                request.originStopId(), request.destinationStopId(), request.departurePointId(),
                amountXof, expiresAt, createdAt);
    }

    public void claimSeat(long caravanId, int seatNumber, String reservationId, LocalDateTime expiresAt) {
        jdbc.update("""
                INSERT INTO sieges_reserves (caravan_id, seat_number, reservation_id, claim_status, expires_at)
                VALUES (?, ?, ?, 'RETENU', ?)
                """, caravanId, seatNumber, reservationId, expiresAt);
    }

    public PendingReservationResponse findPending(String reservationId) {
        try {
            return jdbc.queryForObject("""
                    SELECT reservation_id, reservation_code, status, seat_number, destination_stop_id, amount_xof,
                           hold_expires_at, fare.wave_payment_url
                    FROM reservations reservation
                    JOIN tarifs fare ON fare.caravan_id = reservation.caravan_id
                        AND fare.origin_stop_id = reservation.origin_stop_id
                        AND fare.destination_stop_id = reservation.destination_stop_id
                    WHERE reservation_id = ? AND status = 'EN_ATTENTE_PAIEMENT'
                      AND hold_expires_at > UTC_TIMESTAMP()
                    """, (rs, row) -> new PendingReservationResponse(
                    rs.getString("reservation_id"), rs.getString("reservation_code"),
                    StatutReservation.valueOf(rs.getString("status")), rs.getInt("seat_number"),
                    rs.getLong("destination_stop_id"),
                    rs.getLong("amount_xof"), rs.getString("wave_payment_url"),
                    rs.getTimestamp("hold_expires_at").toLocalDateTime()), reservationId);
        } catch (EmptyResultDataAccessException error) {
            return null;
        }
    }

    public PendingReservationResponse findPendingForCustomer(RecoverReservationRequest request) {
        try {
            return jdbc.queryForObject("""
                    SELECT reservation.reservation_id, reservation.reservation_code, reservation.status,
                           reservation.seat_number, reservation.destination_stop_id, reservation.amount_xof, reservation.hold_expires_at,
                           fare.wave_payment_url
                    FROM reservations reservation
                    JOIN clients client ON client.customer_id = reservation.customer_id
                    JOIN tarifs fare ON fare.caravan_id = reservation.caravan_id
                        AND fare.origin_stop_id = reservation.origin_stop_id
                        AND fare.destination_stop_id = reservation.destination_stop_id
                    WHERE reservation.caravan_id = ? AND reservation.status = 'EN_ATTENTE_PAIEMENT'
                      AND reservation.hold_expires_at > UTC_TIMESTAMP()
                      AND LOWER(TRIM(client.last_name)) = LOWER(TRIM(?))
                      AND TRIM(client.document_number) = TRIM(?)
                    ORDER BY reservation.created_at DESC LIMIT 1
                    """, (rs, row) -> new PendingReservationResponse(
                    rs.getString("reservation_id"), rs.getString("reservation_code"),
                    StatutReservation.valueOf(rs.getString("status")), rs.getInt("seat_number"),
                    rs.getLong("destination_stop_id"),
                    rs.getLong("amount_xof"), rs.getString("wave_payment_url"),
                    rs.getTimestamp("hold_expires_at").toLocalDateTime()),
                    request.caravanId(), request.lastName(), request.documentNumber());
        } catch (EmptyResultDataAccessException error) {
            return null;
        }
    }

    public boolean cancelPending(String reservationId) {
        int updated = jdbc.update("""
                UPDATE reservations SET status = 'ANNULEE'
                WHERE reservation_id = ? AND status = 'EN_ATTENTE_PAIEMENT'
                  AND hold_expires_at > UTC_TIMESTAMP()
                """, reservationId);
        if (updated == 1) {
            jdbc.update("DELETE FROM sieges_reserves WHERE reservation_id = ? AND claim_status = 'RETENU'", reservationId);
            return true;
        }
        return false;
    }
}
