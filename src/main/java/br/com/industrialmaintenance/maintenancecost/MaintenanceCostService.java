
package br.com.industrialmaintenance.maintenancecost;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.maintenancecost.dto.MaintenanceCostReportResponse;
import br.com.industrialmaintenance.maintenancecost.dto.WorkOrderCostResponse;
import br.com.industrialmaintenance.partusage.PartUsage;
import br.com.industrialmaintenance.partusage.PartUsageRepository;
import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MaintenanceCostService {

    private final WorkOrderRepository workOrderRepository;
    private final PartUsageRepository partUsageRepository;

    public MaintenanceCostService(
            WorkOrderRepository workOrderRepository,
            PartUsageRepository partUsageRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.partUsageRepository = partUsageRepository;
    }

    @Transactional(readOnly = true)
    public MaintenanceCostReportResponse getReport() {
        List<WorkOrderCostResponse> workOrderCosts =
                workOrderRepository.findAll()
                        .stream()
                        .map(workOrder -> calculateWorkOrderCost(
                                workOrder.getId()
                        ))
                        .toList();

        BigDecimal totalCost = workOrderCosts.stream()
                .map(WorkOrderCostResponse::partsCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MaintenanceCostReportResponse(
                totalCost,
                workOrderCosts
        );
    }

    @Transactional(readOnly = true)
    public WorkOrderCostResponse getWorkOrderCost(Long workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException(
                    "Work order not found with id: " + workOrderId
            );
        }

        return calculateWorkOrderCost(workOrderId);
    }

    private WorkOrderCostResponse calculateWorkOrderCost(
            Long workOrderId
    ) {
        List<PartUsage> partUsages =
                partUsageRepository.findByWorkOrderId(workOrderId);

        BigDecimal partsCost = partUsages.stream()
                .map(partUsage -> partUsage.getUnitCost()
                        .multiply(BigDecimal.valueOf(
                                partUsage.getQuantity()
                        )))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new WorkOrderCostResponse(workOrderId, partsCost);
    }
}
