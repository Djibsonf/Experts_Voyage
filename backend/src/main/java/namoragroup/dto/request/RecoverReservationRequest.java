package namoragroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecoverReservationRequest(
        @Positive long caravanId,
        @NotBlank String lastName,
        @NotBlank String documentNumber) {}
