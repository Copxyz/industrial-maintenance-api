
package br.com.industrialmaintenance.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record WorkOrderCreateRequest(

        @NotNull(message = "Equipment ID is required")
        @Positive(message = "Equipment ID must be positive")
        Long equipmentId,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description

) {
}
