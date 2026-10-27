
package br.com.industrialmaintenance.workorder.dto;

import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderStatus;

import java.time.LocalDateTime;

public record WorkOrderResponse(
        Long id,
        Long equipmentId,
        String description,
        WorkOrderStatus status,
        LocalDateTime openedAt,
        LocalDateTime completedAt
) {

    public static WorkOrderResponse fromEntity(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getEquipment().getId(),
                workOrder.getDescription(),
                workOrder.getStatus(),
                workOrder.getOpenedAt(),
                workOrder.getCompletedAt()
        );
    }
}
