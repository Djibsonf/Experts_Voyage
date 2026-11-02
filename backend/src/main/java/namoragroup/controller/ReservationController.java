package namoragroup.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import namoragroup.dto.request.CreateReservationRequest;
import namoragroup.dto.request.RecoverReservationRequest;
import namoragroup.dto.response.ReservationCreatedResponse;
import namoragroup.dto.response.PendingReservationResponse;
import namoragroup.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservations;

    public ReservationController(ReservationService reservations) {
        this.reservations = reservations;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationCreatedResponse create(@Valid @RequestBody CreateReservationRequest request) {
        return reservations.reserve(request);
    }

    @PostMapping("/reprendre")
    public PendingReservationResponse recover(@Valid @RequestBody RecoverReservationRequest request) {
        return reservations.recover(request);
    }

    @GetMapping("/{reservationId}")
    public PendingReservationResponse pending(@PathVariable String reservationId) {
        return reservations.findPending(reservationId);
    }

    @PostMapping("/{reservationId}/annuler")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String reservationId) {
        reservations.cancelPending(reservationId);
    }
}
