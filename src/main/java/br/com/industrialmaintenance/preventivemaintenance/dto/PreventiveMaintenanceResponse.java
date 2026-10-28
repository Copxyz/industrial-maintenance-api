
package br.com.industrialmaintenance.preventivemaintenance.dto;

import br.com.industrialmaintenance.preventivemaintenance.PreventiveMaintenance;
import br.com.industrialmaintenance.preventivemaintenance.PreventiveMaintenanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PreventiveMaintenanceResponse(
        Long id,
        Long equipmentId,
        String description,
        LocalDate scheduledDate,
        PreventiveMaintenanceStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {

    public static PreventiveMaintenanceResponse fromEntity(
            PreventiveMaintenance maintenance
    ) {
        return new PreventiveMaintenanceResponse(
                maintenance.getId(),
                maintenance.getEquipment().getId(),
                maintenance.getDescription(),
                maintenance.getScheduledDate(),
                maintenance.getStatus(),
                maintenance.getCreatedAt(),
                maintenance.getCompletedAt()
        );
    }
}
