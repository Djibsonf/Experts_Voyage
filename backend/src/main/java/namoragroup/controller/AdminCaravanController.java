package namoragroup.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import namoragroup.dto.request.CreateCaravanRequest;
import namoragroup.dto.request.UpdateCaravanRequest;
import namoragroup.dto.response.AdminCaravanDtos;
import namoragroup.service.AdminCaravanService;

@RestController
@RequestMapping("/api/admin")
public class AdminCaravanController {
    private final AdminCaravanService caravans;

    public AdminCaravanController(AdminCaravanService caravans) {
        this.caravans = caravans;
    }

    @GetMapping("/tableau-de-bord")
    public Map<String, Long> dashboard() {
        return Map.of("caravanesEnCours", caravans.countCurrent(), "passagers", caravans.countPassengers());
    }

    @GetMapping("/caravanes")
    public List<AdminCaravanDtos.CaravanItem> current() { return caravans.current(); }

    @GetMapping("/caravanes/historique")
    public List<AdminCaravanDtos.CaravanItem> history() { return caravans.history(); }

    @GetMapping("/caravanes/{id}")
    public AdminCaravanDtos.CaravanItem get(@PathVariable long id) { return caravans.get(id); }

    @PostMapping("/caravanes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> create(@Valid @RequestBody CreateCaravanRequest request) {
        return Map.of("id", caravans.create(request));
    }

    @PutMapping("/caravanes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @Valid @RequestBody UpdateCaravanRequest request) {
        caravans.update(id, request);
    }

    @PatchMapping("/caravanes/{id}/archiver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable long id) { caravans.archive(id, true); }

    @PatchMapping("/caravanes/{id}/desarchiver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchive(@PathVariable long id) { caravans.archive(id, false); }

    @GetMapping("/caravanes/{id}/passagers")
    public List<AdminCaravanDtos.Passenger> passengers(@PathVariable long id) {
        return caravans.passengers(id);
    }

    @DeleteMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnpaidReservation(@PathVariable String reservationId) {
        caravans.deleteUnpaidReservation(reservationId);
    }
}
