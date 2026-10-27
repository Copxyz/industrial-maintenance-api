
package br.com.industrialmaintenance.workorder;

import br.com.industrialmaintenance.equipment.Equipment;
import br.com.industrialmaintenance.equipment.EquipmentRepository;
import br.com.industrialmaintenance.exception.BusinessRuleException;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private Equipment equipment;

    private WorkOrderService workOrderService;

    @BeforeEach
    void setUp() {
        workOrderService = new WorkOrderService(
                workOrderRepository,
                equipmentRepository
        );
    }

    @Test
    void shouldCreateWorkOrderSuccessfully() {
        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(1L);
        when(workOrderRepository.save(any(WorkOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = workOrderService.create(1L, "Inspect motor");

        assertEquals(1L, response.equipmentId());
        assertEquals("Inspect motor", response.description());
        assertEquals(WorkOrderStatus.OPEN, response.status());
        verify(workOrderRepository).save(any(WorkOrder.class));
    }

    @Test
    void shouldThrowWhenEquipmentDoesNotExist() {
        when(equipmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> workOrderService.create(99L, "Inspect motor")
        );

        verify(workOrderRepository, never()).save(any(WorkOrder.class));
    }

    @Test
    void shouldThrowWhenStartingWorkOrderThatIsNotOpen() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                BusinessRuleException.class,
                () -> workOrderService.start(1L)
        );

        verify(workOrderRepository, never()).save(any(WorkOrder.class));
    }

    @Test
    void shouldThrowWhenCompletingWorkOrderThatIsNotInProgress() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(WorkOrderStatus.OPEN);

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        assertThrows(
                BusinessRuleException.class,
                () -> workOrderService.complete(1L)
        );

        verify(workOrderRepository, never()).save(any(WorkOrder.class));
    }

    @Test
    void shouldThrowWhenWorkOrderDoesNotExist() {
        when(workOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> workOrderService.findById(99L)
        );
    }
}
