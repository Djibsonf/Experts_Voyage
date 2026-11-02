package namoragroup.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import namoragroup.dto.response.CaravanDtos;

@Repository
public class CaravanRepository {
    private final JdbcTemplate jdbc;

    public CaravanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CaravanDtos.CaravanSummary> findUpcomingOpenCaravans() {
        String sql = """
                SELECT c.caravan_id, c.name AS caravan_code, origin_city.name AS origin_name,
                       destination_city.name AS destination_name, DATE(c.departure_at) AS departure_date,
                       TIME(c.departure_at) AS departure_time,
                       (SELECT dp.name FROM points_depart_caravane csp
                        JOIN lieux_depart dp ON dp.departure_point_id = csp.departure_point_id
                        WHERE csp.caravan_id = c.caravan_id AND csp.stop_id = origin.stop_id
                        ORDER BY csp.pickup_order LIMIT 1) AS departure_point
                FROM caravanes c
                JOIN etapes_caravane origin ON origin.caravan_id = c.caravan_id AND origin.stop_order = 1
                JOIN villes origin_city ON origin_city.city_id = origin.city_id
                JOIN etapes_caravane destination ON destination.caravan_id = c.caravan_id
                    AND destination.stop_order = (SELECT MAX(last_stop.stop_order) FROM etapes_caravane last_stop WHERE last_stop.caravan_id = c.caravan_id)
                JOIN villes destination_city ON destination_city.city_id = destination.city_id
                WHERE c.status = 'OUVERTE' AND c.archived = FALSE AND c.departure_at >= UTC_TIMESTAMP()
                ORDER BY c.departure_at
                """;
        return jdbc.query(sql, (rs, row) -> new CaravanDtos.CaravanSummary(
                rs.getLong("caravan_id"), rs.getString("caravan_code"), rs.getString("origin_name"),
                rs.getString("destination_name"), rs.getDate("departure_date").toLocalDate(),
                rs.getTime("departure_time").toLocalTime(), rs.getString("departure_point")));
    }

    public boolean exists(long caravanId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM caravanes WHERE caravan_id = ?)", Boolean.class, caravanId));
    }

    public CaravanDtos.CaravanDetails findPublicDetails(long caravanId) {
        try {
            return jdbc.queryForObject("""
                    SELECT c.caravan_id, c.name, c.departure_at, bus.seat_capacity,
                           origin.stop_id AS origin_stop_id, origin_city.name AS origin_city,
                           middle.stop_id AS middle_stop_id, middle_city.name AS middle_city,
                           destination.stop_id AS destination_stop_id, destination_city.name AS destination_city
                    FROM caravanes c
                    JOIN bus ON bus.vehicle_id = c.vehicle_id
                    JOIN etapes_caravane origin ON origin.caravan_id = c.caravan_id AND origin.stop_order = 1
                    JOIN villes origin_city ON origin_city.city_id = origin.city_id
                    LEFT JOIN etapes_caravane middle ON middle.caravan_id = c.caravan_id AND middle.stop_order = 2
                    LEFT JOIN villes middle_city ON middle_city.city_id = middle.city_id
                    JOIN etapes_caravane destination ON destination.caravan_id = c.caravan_id
                        AND destination.stop_order = (SELECT MAX(last_stop.stop_order) FROM etapes_caravane last_stop WHERE last_stop.caravan_id = c.caravan_id)
                    JOIN villes destination_city ON destination_city.city_id = destination.city_id
                    WHERE c.caravan_id = ? AND c.status = 'OUVERTE' AND c.archived = FALSE
                      AND c.departure_at >= UTC_TIMESTAMP()
                    """, (rs, row) -> new CaravanDtos.CaravanDetails(
                    rs.getLong("caravan_id"), rs.getString("name"), rs.getString("origin_city"),
                    rs.getString("middle_city"), rs.getString("destination_city"),
                    rs.getTimestamp("departure_at").toLocalDateTime(), rs.getLong("origin_stop_id"),
                    rs.getLong("middle_stop_id"), rs.getLong("destination_stop_id"), rs.getInt("seat_capacity")), caravanId);
        } catch (org.springframework.dao.EmptyResultDataAccessException error) {
            return null;
        }
    }

    public List<CaravanDtos.DestinationOption> findDestinations(long caravanId, long originStopId) {
        return jdbc.query("""
                SELECT destination.stop_id, city.city_code, city.name, fare.amount_xof, fare.wave_payment_url
                FROM tarifs fare
                JOIN etapes_caravane origin ON origin.stop_id = fare.origin_stop_id AND origin.caravan_id = fare.caravan_id
                JOIN etapes_caravane destination ON destination.stop_id = fare.destination_stop_id AND destination.caravan_id = fare.caravan_id
                JOIN villes city ON city.city_id = destination.city_id
                JOIN caravanes c ON c.caravan_id = fare.caravan_id
                WHERE fare.caravan_id = ? AND origin.stop_id = ?
                  AND destination.stop_order > origin.stop_order AND c.status = 'OUVERTE'
                  AND c.archived = FALSE AND c.departure_at >= UTC_TIMESTAMP()
                ORDER BY destination.stop_order
                """, (rs, row) -> new CaravanDtos.DestinationOption(
                rs.getLong("stop_id"), rs.getString("city_code"), rs.getString("name"),
                rs.getLong("amount_xof"), rs.getString("wave_payment_url")),
                caravanId, originStopId);
    }

    public List<CaravanDtos.DeparturePoint> findDeparturePoints(long caravanId, long originStopId) {
        return jdbc.query("""
                SELECT dp.departure_point_id, dp.name, csp.pickup_order, TIME(csp.departure_at) AS departure_time
                FROM points_depart_caravane csp
                JOIN lieux_depart dp ON dp.departure_point_id = csp.departure_point_id
                JOIN etapes_caravane stop ON stop.stop_id = csp.stop_id AND stop.caravan_id = csp.caravan_id
                JOIN caravanes caravan ON caravan.caravan_id = csp.caravan_id
                WHERE csp.caravan_id = ? AND stop.stop_id = ? AND dp.active = TRUE
                  AND caravan.archived = FALSE AND caravan.status = 'OUVERTE'
                  AND caravan.departure_at >= UTC_TIMESTAMP()
                ORDER BY csp.pickup_order
                """, (rs, row) -> new CaravanDtos.DeparturePoint(
                rs.getLong("departure_point_id"), rs.getString("name"), rs.getInt("pickup_order"),
                rs.getTime("departure_time") == null ? null : rs.getTime("departure_time").toLocalTime()),
                caravanId, originStopId);
    }

    public boolean fareExists(long caravanId, long originStopId, long destinationStopId) {
        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM tarifs fare
                JOIN etapes_caravane origin ON origin.stop_id = fare.origin_stop_id AND origin.caravan_id = fare.caravan_id
                JOIN etapes_caravane destination ON destination.stop_id = fare.destination_stop_id AND destination.caravan_id = fare.caravan_id
                JOIN caravanes caravan ON caravan.caravan_id = fare.caravan_id
                WHERE fare.caravan_id = ? AND origin.stop_id = ? AND destination.stop_id = ?
                  AND origin.stop_order < destination.stop_order
                  AND caravan.archived = FALSE AND caravan.status = 'OUVERTE'
                  AND caravan.departure_at >= UTC_TIMESTAMP()
                """, Integer.class, caravanId, originStopId, destinationStopId);
        return count != null && count > 0;
    }

    public List<CaravanDtos.SeatOption> findSeats(long caravanId) {
        return jdbc.query("""
                SELECT seats.seat_number, seats.seat_row, seats.position_in_row, seats.side, seats.is_back_row,
                       CASE WHEN claim.claim_status = 'CONFIRME' THEN 'RESERVEE'
                            WHEN claim.claim_status = 'RETENU' AND claim.expires_at > UTC_TIMESTAMP() THEN 'RETENUE'
                            ELSE 'DISPONIBLE' END AS seat_status
                FROM sieges_caravane seats
                LEFT JOIN sieges_reserves claim ON claim.caravan_id = seats.caravan_id AND claim.seat_number = seats.seat_number
                WHERE seats.caravan_id = ?
                ORDER BY seats.seat_number
                """, (rs, row) -> new CaravanDtos.SeatOption(
                rs.getInt("seat_number"), integer(rs.getObject("seat_row")), integer(rs.getObject("position_in_row")),
                rs.getString("side"), rs.getBoolean("is_back_row"), rs.getString("seat_status")), caravanId);
    }

    private Integer integer(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }
}
