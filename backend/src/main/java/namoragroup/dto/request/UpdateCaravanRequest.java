package namoragroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateCaravanRequest(
        @NotBlank @Size(max = 100) String nom,
        @NotNull LocalDateTime departLe) {}
