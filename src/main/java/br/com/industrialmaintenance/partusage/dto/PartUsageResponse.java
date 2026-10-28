
package br.com.industrialmaintenance.partusage.dto;

import br.com.industrialmaintenance.partusage.PartUsage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PartUsageResponse(
        Long id,
        Long workOrderId,
        String partName,
        Integer quantity,
        BigDecimal unitCost,
        BigDecimal totalCost,
        LocalDateTime recordedAt
) {

    public static PartUsageResponse fromEntity(PartUsage partUsage) {
        BigDecimal totalCost = partUsage.getUnitCost()
                .multiply(BigDecimal.valueOf(partUsage.getQuantity()));

        return new PartUsageResponse(
                partUsage.getId(),
                partUsage.getWorkOrder().getId(),
                partUsage.getPartName(),
                partUsage.getQuantity(),
                partUsage.getUnitCost(),
                totalCost,
                partUsage.getRecordedAt()
        );
    }
}
