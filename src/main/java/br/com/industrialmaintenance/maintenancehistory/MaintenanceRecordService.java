
package br.com.industrialmaintenance.maintenancehistory;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordCreateRequest;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordResponse;
import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository recordRepository;
    private final WorkOrderRepository workOrderRepository;

    public MaintenanceRecordService(
            MaintenanceRecordRepository recordRepository,
            WorkOrderRepository workOrderRepository
    ) {
        this.recordRepository = recordRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public MaintenanceRecordResponse create(
            MaintenanceRecordCreateRequest request
    ) {
        WorkOrder workOrder = workOrderRepository.findById(request.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work order not found with id: " + request.workOrderId()
                ));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setWorkOrder(workOrder);
        record.setFailureDescription(request.failureDescription());
        record.setSolutionApplied(request.solutionApplied());
        record.setNotes(request.notes());

        MaintenanceRecord savedRecord = recordRepository.save(record);

        return MaintenanceRecordResponse.fromEntity(savedRecord);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecordResponse> findByWorkOrderId(Long workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException(
                    "Work order not found with id: " + workOrderId
            );
        }

        return recordRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(MaintenanceRecordResponse::fromEntity)
                .toList();
    }
}
