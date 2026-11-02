package namoragroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import namoragroup.model.enums.IdentityDocumentType;

public record CreateReservationRequest(
        @NotNull Long caravanId,
        @NotNull Integer seatNumber,
        @NotNull Long originStopId,
        @NotNull Long destinationStopId,
        @NotNull Long departurePointId,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull IdentityDocumentType documentType,
        @NotBlank @Size(min = 4, max = 80) @Pattern(regexp = "[A-Za-z0-9 .\\-/]+") String documentNumber) {}
