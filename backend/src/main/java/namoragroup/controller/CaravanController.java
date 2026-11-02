package namoragroup.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import namoragroup.dto.response.CaravanDtos;
import namoragroup.exception.ApiException;
import namoragroup.repository.CaravanRepository;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/api/caravanes")
public class CaravanController {
    private final CaravanRepository caravans;

    public CaravanController(CaravanRepository caravans) {
        this.caravans = caravans;
    }

    @GetMapping("/en-cours")
    public List<CaravanDtos.CaravanSummary> currentCaravans() {
        return caravans.findUpcomingOpenCaravans();
    }

    @GetMapping("/{caravanId}")
    public CaravanDtos.CaravanDetails caravanDetails(@PathVariable long caravanId) {
        CaravanDtos.CaravanDetails details = caravans.findPublicDetails(caravanId);
        if (details == null) throw new ApiException(NOT_FOUND, "CARAVAN_NOT_FOUND", "Cette caravane n’est plus disponible.");
        return details;
    }

    @GetMapping("/{caravanId}/destinations")
    public List<CaravanDtos.DestinationOption> destinations(
            @PathVariable long caravanId, @RequestParam long originStopId) {
        if (!caravans.exists(caravanId)) {
            throw new ApiException(NOT_FOUND, "CARAVAN_NOT_FOUND", "Cette caravane n’existe pas.");
        }
        List<CaravanDtos.DestinationOption> options = caravans.findDestinations(caravanId, originStopId);
        if (options.isEmpty()) {
            throw new ApiException(UNPROCESSABLE_ENTITY, "NO_DESTINATIONS", "Aucune destination tarifée n’est disponible depuis ce départ.");
        }
        return options;
    }

    @GetMapping("/{caravanId}/points-de-depart")
    public List<CaravanDtos.DeparturePoint> departurePoints(
            @PathVariable long caravanId, @RequestParam long originStopId) {
        if (!caravans.exists(caravanId)) {
            throw new ApiException(NOT_FOUND, "CARAVAN_NOT_FOUND", "Cette caravane n’existe pas.");
        }
        return caravans.findDeparturePoints(caravanId, originStopId);
    }

    @GetMapping("/{caravanId}/sieges")
    public List<CaravanDtos.SeatOption> seats(
            @PathVariable long caravanId,
            @RequestParam long originStopId,
            @RequestParam long destinationStopId) {
        if (!caravans.exists(caravanId)) {
            throw new ApiException(NOT_FOUND, "CARAVAN_NOT_FOUND", "Cette caravane n’existe pas.");
        }
        if (!caravans.fareExists(caravanId, originStopId, destinationStopId)) {
            throw new ApiException(UNPROCESSABLE_ENTITY, "INVALID_TRIP", "Le trajet sélectionné n’est pas disponible pour cette caravane.");
        }
        return caravans.findSeats(caravanId);
    }
}
