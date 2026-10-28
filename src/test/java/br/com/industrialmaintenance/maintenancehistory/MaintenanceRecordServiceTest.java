
package br.com.industrialmaintenance.maintenancehistory;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordCreateRequest;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordResponse;
import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceRecordServiceTest {

    @Mock
    private MaintenanceRecordRepository recordRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private MaintenanceRecordService maintenanceRecordService;

    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        workOrder = spy(new WorkOrder());
    }

    @Test
    void shouldCreateMaintenanceRecord() {
        MaintenanceRecordCreateRequest request =
                new MaintenanceRecordCreateRequest(
                        1L,
                        "Motor superaquecendo",
                        "Limpeza do sistema de ventilação",
                        "Temperatura normalizada após o reparo"
                );

        doReturn(1L).when(workOrder).getId();

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(recordRepository.save(any(MaintenanceRecord.class)))
                .thenAnswer(invocation -> {
                    MaintenanceRecord record = invocation.getArgument(0);

                    MaintenanceRecord savedRecord = spy(record);
                    doReturn(10L).when(savedRecord).getId();

                    return savedRecord;
                });

        MaintenanceRecordResponse response =
                maintenanceRecordService.create(request);

        assertEquals(10L, response.id());
        assertEquals(1L, response.workOrderId());
        assertEquals("Motor superaquecendo", response.failureDescription());
        assertEquals(
                "Limpeza do sistema de ventilação",
                response.solutionApplied()
        );

        verify(recordRepository).save(any(MaintenanceRecord.class));
    }

    @Test
    void shouldThrowWhenCreatingRecordForMissingWorkOrder() {
        MaintenanceRecordCreateRequest request =
                new MaintenanceRecordCreateRequest(
                        999L,
                        "Falha",
                        "Reparo",
                        null
                );

        when(workOrderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> maintenanceRecordService.create(request)
        );

        verify(recordRepository, never()).save(any());
    }

    @Test
    void shouldReturnRecordsForWorkOrder() {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setWorkOrder(workOrder);
        record.setFailureDescription("Vibração excessiva");
        record.setSolutionApplied("Ajuste do equipamento");

        doReturn(1L).when(workOrder).getId();

        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(recordRepository.findByWorkOrderId(1L))
                .thenReturn(List.of(record));

        List<MaintenanceRecordResponse> responses =
                maintenanceRecordService.findByWorkOrderId(1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).workOrderId());
        assertEquals(
                "Vibração excessiva",
                responses.get(0).failureDescription()
        );
    }

    @Test
    void shouldReturnEmptyListWhenWorkOrderHasNoRecords() {
        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(recordRepository.findByWorkOrderId(1L))
                .thenReturn(List.of());

        List<MaintenanceRecordResponse> responses =
                maintenanceRecordService.findByWorkOrderId(1L);

        assertTrue(responses.isEmpty());
    }

    @Test
    void shouldThrowWhenSearchingRecordsForMissingWorkOrder() {
        when(workOrderRepository.existsById(999L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> maintenanceRecordService.findByWorkOrderId(999L)
        );

        verify(recordRepository, never()).findByWorkOrderId(any());
    }
}
