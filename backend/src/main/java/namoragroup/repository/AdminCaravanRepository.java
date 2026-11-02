package namoragroup.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import namoragroup.dto.request.CreateCaravanRequest;
import namoragroup.dto.request.UpdateCaravanRequest;
import namoragroup.dto.response.AdminCaravanDtos;

@Repository
public class AdminCaravanRepository {
    private final JdbcTemplate jdbc;

    public AdminCaravanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long create(CreateCaravanRequest request) {
        long villeOrigineId = obtenirVille(request.villeDepart());
        boolean departDakar = "Dakar".equalsIgnoreCase(request.villeDepart());
        String nomDestination = departDakar ? "Ziguinchor" : "Dakar";
        long villeDestinationId = obtenirVille(nomDestination);
        long villeKaolackId = obtenirVille("Kaolack");
        long busId = creerBusEtSieges(request.nom(), request.capacite());
        String code = "EV-" + request.departLe().getYear() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long caravaneId = insertAndGetId("""
                INSERT INTO caravanes (vehicle_id, caravan_code, name, status, departure_at, archived)
                VALUES (?, ?, ?, 'OUVERTE', ?, FALSE)
                """, busId, code, request.nom().trim(), Timestamp.valueOf(request.departLe()));
        jdbc.update("""
                INSERT INTO sieges_caravane
                    (caravan_id, seat_number, seat_row, position_in_row, side, is_back_row)
                SELECT ?, seat_number, seat_row, position_in_row, side, is_back_row
                FROM sieges_bus WHERE vehicle_id = ?
                """, caravaneId, busId);

        long departId = creerEtape(caravaneId, villeOrigineId, 1);
        long kaolackId = creerEtape(caravaneId, villeKaolackId, 2);
        long arriveeId = creerEtape(caravaneId, villeDestinationId, 3);
        List<String> lieuxDepart = departDakar
                ? List.of("Bountou Pikine", "Sedima")
                : List.of("Entre Pavillon");
        for (int index = 0; index < lieuxDepart.size(); index++) {
            long lieuDepartId = obtenirLieuDepart(villeOrigineId, lieuxDepart.get(index));
            jdbc.update("""
                    INSERT INTO points_depart_caravane (caravan_id, stop_id, departure_point_id, pickup_order)
                    VALUES (?, ?, ?, ?)
                    """, caravaneId, departId, lieuDepartId, index + 1);
        }
        if (request.prixDepartKaolack() != null) {
            insertTarif(caravaneId, departId, kaolackId, request.prixDepartKaolack(), request.lienWaveDepartKaolack());
        }
        insertTarif(caravaneId, departId, arriveeId, request.prixDepartDestination(), request.lienWaveDepartDestination());
        if (request.prixKaolackDestination() != null) {
            insertTarif(caravaneId, kaolackId, arriveeId, request.prixKaolackDestination(), request.lienWaveKaolackDestination());
        }
        return caravaneId;
    }

    public List<AdminCaravanDtos.CaravanItem> findCurrent() {
        return findByFilter("c.archived = FALSE AND c.status = 'OUVERTE' AND c.departure_at >= UTC_TIMESTAMP()");
    }

    public List<AdminCaravanDtos.CaravanItem> findHistory() {
        return findByFilter("c.archived = TRUE OR c.status IN ('TERMINEE', 'ANNULEE') OR c.departure_at < UTC_TIMESTAMP()");
    }

    public AdminCaravanDtos.CaravanItem findOne(long caravanId) {
        List<AdminCaravanDtos.CaravanItem> result = findByFilter("c.caravan_id = " + caravanId);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<AdminCaravanDtos.Passenger> findPassengers(long caravanId) {
        return jdbc.query("""
                SELECT r.reservation_id, r.reservation_code, client.first_name, client.last_name,
                       client.document_type, client.document_number, r.seat_number,
                       origin_city.name AS ville_depart, departure.name AS lieu_depart,
                       destination_city.name AS ville_arrivee, r.amount_xof, r.status, r.created_at
                FROM reservations r
                JOIN clients client ON client.customer_id = r.customer_id
                JOIN etapes_caravane origin ON origin.stop_id = r.origin_stop_id AND origin.caravan_id = r.caravan_id
                JOIN villes origin_city ON origin_city.city_id = origin.city_id
                JOIN lieux_depart departure ON departure.departure_point_id = r.departure_point_id
                JOIN etapes_caravane destination ON destination.stop_id = r.destination_stop_id AND destination.caravan_id = r.caravan_id
                JOIN villes destination_city ON destination_city.city_id = destination.city_id
                WHERE r.caravan_id = ? AND r.status NOT IN ('ANNULEE', 'EXPIREE')
                ORDER BY r.seat_number
                """, (rs, row) -> new AdminCaravanDtos.Passenger(
                rs.getString("reservation_id"), rs.getString("reservation_code"),
                rs.getString("first_name"), rs.getString("last_name"),
                rs.getString("document_type"), rs.getString("document_number"), rs.getInt("seat_number"),
                rs.getString("ville_depart"), rs.getString("lieu_depart"), rs.getString("ville_arrivee"),
                rs.getLong("amount_xof"), rs.getString("status"), rs.getTimestamp("created_at").toLocalDateTime()), caravanId);
    }

    public boolean exists(long caravanId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM caravanes WHERE caravan_id = ?", Integer.class, caravanId);
        return count != null && count > 0;
    }

    public void update(long caravanId, UpdateCaravanRequest request) {
        jdbc.update("UPDATE caravanes SET name = ?, departure_at = ? WHERE caravan_id = ?",
                request.nom().trim(), Timestamp.valueOf(request.departLe()), caravanId);
    }

    public void setArchived(long caravanId, boolean archived) {
        jdbc.update("UPDATE caravanes SET archived = ? WHERE caravan_id = ?", archived, caravanId);
    }

    public long countCurrent() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM caravanes WHERE archived = FALSE AND status = 'OUVERTE' AND departure_at >= UTC_TIMESTAMP()", Long.class);
        return count == null ? 0 : count;
    }

    public long countPassengers() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM reservations WHERE status NOT IN ('ANNULEE', 'EXPIREE')", Long.class);
        return count == null ? 0 : count;
    }

    public boolean deleteUnpaidReservation(String reservationId) {
        Long clientId;
        String status;
        try {
            status = jdbc.queryForObject("SELECT status FROM reservations WHERE reservation_id = ?", String.class, reservationId);
            clientId = jdbc.queryForObject("SELECT customer_id FROM reservations WHERE reservation_id = ?", Long.class, reservationId);
        } catch (EmptyResultDataAccessException error) {
            return false;
        }
        if (!"EN_ATTENTE_PAIEMENT".equals(status) && !"EXPIREE".equals(status) && !"ANNULEE".equals(status)) {
            throw new IllegalStateException("Seules les réservations non payées peuvent être supprimées.");
        }
        jdbc.update("DELETE FROM paiements WHERE reservation_id = ?", reservationId);
        jdbc.update("DELETE FROM tickets WHERE reservation_id = ?", reservationId);
        jdbc.update("DELETE FROM sieges_reserves WHERE reservation_id = ?", reservationId);
        jdbc.update("DELETE FROM reservations WHERE reservation_id = ?", reservationId);
        jdbc.update("DELETE FROM clients WHERE customer_id = ? AND NOT EXISTS (SELECT 1 FROM reservations WHERE customer_id = ?)", clientId, clientId);
        return true;
    }

    private List<AdminCaravanDtos.CaravanItem> findByFilter(String filter) {
        return jdbc.query("""
                SELECT c.caravan_id, c.name, origin_city.name AS ville_depart,
                       destination_city.name AS ville_arrivee, c.departure_at, c.status, c.archived,
                       bus.seat_capacity,
                       (SELECT COUNT(*) FROM reservations r WHERE r.caravan_id = c.caravan_id
                         AND r.status NOT IN ('ANNULEE', 'EXPIREE')) AS reservation_count
                FROM caravanes c
                JOIN bus ON bus.vehicle_id = c.vehicle_id
                JOIN etapes_caravane origin ON origin.caravan_id = c.caravan_id AND origin.stop_order = 1
                JOIN villes origin_city ON origin_city.city_id = origin.city_id
                JOIN etapes_caravane destination ON destination.caravan_id = c.caravan_id AND destination.stop_order = 3
                JOIN villes destination_city ON destination_city.city_id = destination.city_id
                WHERE %s
                ORDER BY c.departure_at DESC
                """.formatted(filter), (rs, row) -> new AdminCaravanDtos.CaravanItem(
                rs.getLong("caravan_id"), rs.getString("name"), rs.getString("ville_depart"),
                rs.getString("ville_arrivee"), rs.getTimestamp("departure_at").toLocalDateTime(),
                rs.getString("status"), rs.getBoolean("archived"), rs.getInt("seat_capacity"),
                rs.getInt("reservation_count")));
    }

    private long obtenirVille(String name) {
        Map<String, String> codes = Map.of("Dakar", "DAK", "Ziguinchor", "ZIG", "Kaolack", "KAO");
        String normalized = name.equalsIgnoreCase("Dakar") ? "Dakar" : name.equalsIgnoreCase("Ziguinchor") ? "Ziguinchor" : name.equalsIgnoreCase("Kaolack") ? "Kaolack" : null;
        if (normalized == null) throw new IllegalArgumentException("Ville non prise en charge.");
        jdbc.update("INSERT IGNORE INTO villes (city_code, name, active) VALUES (?, ?, TRUE)", codes.get(normalized), normalized);
        return jdbc.queryForObject("SELECT city_id FROM villes WHERE city_code = ?", Long.class, codes.get(normalized));
    }

    private long obtenirLieuDepart(long villeId, String name) {
        jdbc.update("INSERT IGNORE INTO lieux_depart (city_id, name, active) VALUES (?, ?, TRUE)", villeId, name.trim());
        return jdbc.queryForObject("SELECT departure_point_id FROM lieux_depart WHERE city_id = ? AND name = ?",
                Long.class, villeId, name.trim());
    }

    private long creerBusEtSieges(String name, int capacity) {
        long busId = insertAndGetId("""
                INSERT INTO bus (label, seat_capacity, layout_confirmed, active)
                VALUES (?, ?, FALSE, TRUE)
                """, name.trim(), capacity);
        jdbc.batchUpdate("INSERT INTO sieges_bus (vehicle_id, seat_number, is_back_row) VALUES (?, ?, FALSE)",
                java.util.stream.IntStream.rangeClosed(1, capacity).mapToObj(number -> new Object[]{busId, number}).toList());
        return busId;
    }

    private long creerEtape(long caravanId, long cityId, int order) {
        return insertAndGetId("""
                INSERT INTO etapes_caravane (caravan_id, city_id, stop_order)
                VALUES (?, ?, ?)
                """, caravanId, cityId, order);
    }

    private void insertTarif(long caravanId, long origin, long destination, long amount, String waveLink) {
        jdbc.update("""
                INSERT INTO tarifs (caravan_id, origin_stop_id, destination_stop_id, amount_xof, wave_payment_url)
                VALUES (?, ?, ?, ?, ?)
                """, caravanId, origin, destination, amount, waveLink.trim());
    }

    private long insertAndGetId(String sql, Object... parameters) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("L'enregistrement n'a pas pu être créé.");
        return key.longValue();
    }
}
