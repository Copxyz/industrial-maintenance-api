
package br.com.industrialmaintenance.partusage.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PartUsageCreateRequest(

        @NotNull(message = "Work order ID is required")
        @Positive(message = "Work order ID must be positive")
        Long workOrderId,

        @NotBlank(message = "Part name is required")
        @Size(max = 120, message = "Part name must not exceed 120 characters")
        String partName,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.00", message = "Unit cost must not be negative")
        BigDecimal unitCost
) {
}
