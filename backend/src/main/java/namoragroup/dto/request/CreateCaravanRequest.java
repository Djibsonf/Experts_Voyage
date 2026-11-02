package namoragroup.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateCaravanRequest(
        @NotBlank @Size(max = 100) String nom,
        @NotBlank String villeDepart,
        @NotNull LocalDateTime departLe,
        @Min(1) @Max(100) int capacite,
        @Positive Long prixDepartKaolack,
        String lienWaveDepartKaolack,
        @Positive long prixDepartDestination,
        @NotBlank String lienWaveDepartDestination,
        @Positive Long prixKaolackDestination,
        String lienWaveKaolackDestination) {}
