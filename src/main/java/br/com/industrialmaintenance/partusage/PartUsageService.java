
package br.com.industrialmaintenance.partusage;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.partusage.dto.PartUsageCreateRequest;
import br.com.industrialmaintenance.partusage.dto.PartUsageResponse;
import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartUsageService {

    private final PartUsageRepository partUsageRepository;
    private final WorkOrderRepository workOrderRepository;

    public PartUsageService(
            PartUsageRepository partUsageRepository,
            WorkOrderRepository workOrderRepository
    ) {
        this.partUsageRepository = partUsageRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public PartUsageResponse create(PartUsageCreateRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(request.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work order not found with id: " + request.workOrderId()
                ));

        PartUsage partUsage = new PartUsage();
        partUsage.setWorkOrder(workOrder);
        partUsage.setPartName(request.partName());
        partUsage.setQuantity(request.quantity());
        partUsage.setUnitCost(request.unitCost());

        PartUsage savedPartUsage = partUsageRepository.save(partUsage);

        return PartUsageResponse.fromEntity(savedPartUsage);
    }

    @Transactional(readOnly = true)
    public List<PartUsageResponse> findByWorkOrderId(Long workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException(
                    "Work order not found with id: " + workOrderId
            );
        }

        return partUsageRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(PartUsageResponse::fromEntity)
                .toList();
    }
}
