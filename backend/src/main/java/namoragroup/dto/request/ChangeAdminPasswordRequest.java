package namoragroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeAdminPasswordRequest(
        @NotBlank String ancienMotDePasse,
        @NotBlank @Size(min = 8, max = 72) String nouveauMotDePasse) {}
