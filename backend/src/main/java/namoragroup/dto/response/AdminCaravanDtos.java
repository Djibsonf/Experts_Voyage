package namoragroup.dto.response;

import java.time.LocalDateTime;

public final class AdminCaravanDtos {
    private AdminCaravanDtos() {}

    public record CaravanItem(long id, String nom, String villeDepart, String villeArrivee,
                              LocalDateTime departLe, String statut, boolean archivee,
                              int capacite, int reservations) {}

    public record Passenger(String reservationId, String codeReservation, String prenom, String nom,
                            String typeDocument, String numeroDocument, int siege,
                            String villeDepart, String lieuDepart, String villeArrivee,
                            long montantXof, String statutPaiement, LocalDateTime reserveLe) {}

    public record AdminProfile(String nomComplet, String email, String role) {}
}
