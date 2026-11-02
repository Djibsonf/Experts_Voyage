package namoragroup.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class CaravanDtos {
    private CaravanDtos() {}

    public record CaravanSummary(
            long id, String code, String villeDepart, String villeArrivee,
            LocalDate dateDepart, LocalTime heureDepart, String lieuDepart) {}

    public record CaravanDetails(
            long id, String nom, String villeDepart, String villeIntermediaire, String villeArrivee,
            LocalDateTime departLe, long originStopId, long intermediateStopId,
            long destinationStopId, int capacite) {}

    public record DestinationOption(long stopId, String cityCode, String ville, long prixXof, String lienPaiementWave) {}

    public record DeparturePoint(long id, String nom, int ordre, LocalTime heureDepart) {}

    public record SeatOption(
            int numero, Integer rangee, Integer position, String cote,
            boolean rangeeArriere, String statut) {}
}
