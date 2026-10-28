
package br.com.industrialmaintenance.maintenancehistory.dto;

import br.com.industrialmaintenance.maintenancehistory.MaintenanceRecord;

import java.time.LocalDateTime;

public record MaintenanceRecordResponse(
        Long id,
        Long workOrderId,
        String failureDescription,
        String solutionApplied,
        String notes,
        LocalDateTime recordedAt
) {

    public static MaintenanceRecordResponse fromEntity(
            MaintenanceRecord record
    ) {
        return new MaintenanceRecordResponse(
                record.getId(),
                record.getWorkOrder().getId(),
                record.getFailureDescription(),
                record.getSolutionApplied(),
                record.getNotes(),
                record.getRecordedAt()
        );
    }
}
