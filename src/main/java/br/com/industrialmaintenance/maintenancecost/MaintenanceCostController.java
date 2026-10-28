
package br.com.industrialmaintenance.maintenancecost;

import br.com.industrialmaintenance.maintenancecost.dto.MaintenanceCostReportResponse;
import br.com.industrialmaintenance.maintenancecost.dto.WorkOrderCostResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance-costs")
public class MaintenanceCostController {

    private final MaintenanceCostService maintenanceCostService;

    public MaintenanceCostController(
            MaintenanceCostService maintenanceCostService
    ) {
        this.maintenanceCostService = maintenanceCostService;
    }

    @GetMapping
    public MaintenanceCostReportResponse getReport() {
        return maintenanceCostService.getReport();
    }

    @GetMapping("/work-order/{workOrderId}")
    public WorkOrderCostResponse getWorkOrderCost(
            @PathVariable Long workOrderId
    ) {
        return maintenanceCostService.getWorkOrderCost(workOrderId);
    }
}
