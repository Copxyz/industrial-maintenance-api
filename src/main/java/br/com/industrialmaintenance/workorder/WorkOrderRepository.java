package br.com.industrialmaintenance.workorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findByEquipmentId(Long equipmentId);
}