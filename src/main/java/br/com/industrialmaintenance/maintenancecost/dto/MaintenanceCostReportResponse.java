
package br.com.industrialmaintenance.maintenancecost.dto;

import java.math.BigDecimal;
import java.util.List;

public record MaintenanceCostReportResponse(
        BigDecimal totalCost,
        List<WorkOrderCostResponse> workOrders
) {
}
