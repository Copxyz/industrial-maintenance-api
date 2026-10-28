
package br.com.industrialmaintenance.preventivemaintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PreventiveMaintenanceCreateRequest(

        @NotNull(message = "Equipment ID is required")
        @Positive(message = "Equipment ID must be positive")
        Long equipmentId,

        @NotBlank(message = "Description is required")
        @Size(
                max = 1000,
                message = "Description must not exceed 1000 characters"
        )
        String description,

        @NotNull(message = "Scheduled date is required")
        LocalDate scheduledDate

) {
}
