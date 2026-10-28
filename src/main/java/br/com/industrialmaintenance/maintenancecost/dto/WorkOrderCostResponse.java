
package br.com.industrialmaintenance.maintenancecost.dto;

import java.math.BigDecimal;

public record WorkOrderCostResponse(
        Long workOrderId,
        BigDecimal partsCost
) {
}
