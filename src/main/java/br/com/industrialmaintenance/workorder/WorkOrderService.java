
package br.com.industrialmaintenance.workorder;

import br.com.industrialmaintenance.workorder.dto.WorkOrderResponse;

import br.com.industrialmaintenance.exception.BusinessRuleException;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;

import br.com.industrialmaintenance.equipment.Equipment;
import br.com.industrialmaintenance.equipment.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            EquipmentRepository equipmentRepository) {
        this.workOrderRepository = workOrderRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public WorkOrderResponse create(Long equipmentId, String description) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + equipmentId));

        WorkOrder workOrder = new WorkOrder();
        workOrder.setEquipment(equipment);
        workOrder.setDescription(description);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        return WorkOrderResponse.fromEntity(savedWorkOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> findAll() {
        return workOrderRepository.findAll()
                .stream()
                .map(WorkOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse findById(Long id) {
        return WorkOrderResponse.fromEntity(getWorkOrderEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> findByStatus(WorkOrderStatus status) {
        return workOrderRepository.findByStatus(status)
                .stream()
                .map(WorkOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> findByEquipmentId(Long equipmentId) {
        return workOrderRepository.findByEquipmentId(equipmentId)
                .stream()
                .map(WorkOrderResponse::fromEntity)
                .toList();
    }

    @Transactional
    public WorkOrderResponse start(Long id) {
        WorkOrder workOrder = getWorkOrderEntityById(id);

        if (workOrder.getStatus() != WorkOrderStatus.OPEN) {
            throw new BusinessRuleException(
                    "Only open work orders can be started");
        }

        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        return WorkOrderResponse.fromEntity(savedWorkOrder);
    }

    @Transactional
    public WorkOrderResponse complete(Long id) {
        WorkOrder workOrder = getWorkOrderEntityById(id);

        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                    "Only in-progress work orders can be completed");
        }

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompletedAt(LocalDateTime.now());

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        return WorkOrderResponse.fromEntity(savedWorkOrder);
    }

    private WorkOrder getWorkOrderEntityById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work order not found with id: " + id));
    }

}
