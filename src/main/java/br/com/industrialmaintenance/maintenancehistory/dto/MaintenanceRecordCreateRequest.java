
package br.com.industrialmaintenance.maintenancehistory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MaintenanceRecordCreateRequest(

        @NotNull(message = "Work order ID is required")
        @Positive(message = "Work order ID must be positive")
        Long workOrderId,

        @NotBlank(message = "Failure description is required")
        @Size(max = 1000, message = "Failure description must not exceed 1000 characters")
        String failureDescription,

        @NotBlank(message = "Solution applied is required")
        @Size(max = 1000, message = "Solution applied must not exceed 1000 characters")
        String solutionApplied,

        @Size(max = 2000, message = "Notes must not exceed 2000 characters")
        String notes
) {
}
