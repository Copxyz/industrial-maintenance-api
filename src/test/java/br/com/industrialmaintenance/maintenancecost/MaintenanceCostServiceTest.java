
package br.com.industrialmaintenance.maintenancecost;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.maintenancecost.dto.MaintenanceCostReportResponse;
import br.com.industrialmaintenance.maintenancecost.dto.WorkOrderCostResponse;
import br.com.industrialmaintenance.partusage.PartUsage;
import br.com.industrialmaintenance.partusage.PartUsageRepository;
import br.com.industrialmaintenance.workorder.WorkOrder;
import br.com.industrialmaintenance.workorder.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceCostServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private PartUsageRepository partUsageRepository;

    @InjectMocks
    private MaintenanceCostService maintenanceCostService;

    @Spy
    private WorkOrder firstWorkOrder = new WorkOrder();

    @Spy
    private WorkOrder secondWorkOrder = new WorkOrder();

    @Test
    void shouldCalculateCostForWorkOrder() {
        PartUsage firstPart = createPartUsage(
                "Rolamento", 2, "25.50"
        );
        PartUsage secondPart = createPartUsage(
                "Filtro", 1, "15.00"
        );

        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(partUsageRepository.findByWorkOrderId(1L))
                .thenReturn(List.of(firstPart, secondPart));

        WorkOrderCostResponse response =
                maintenanceCostService.getWorkOrderCost(1L);

        assertEquals(1L, response.workOrderId());
        assertEquals(
                0,
                new BigDecimal("66.00").compareTo(response.partsCost())
        );
    }

    @Test
    void shouldReturnZeroWhenWorkOrderHasNoParts() {
        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(partUsageRepository.findByWorkOrderId(1L))
                .thenReturn(List.of());

        WorkOrderCostResponse response =
                maintenanceCostService.getWorkOrderCost(1L);

        assertEquals(BigDecimal.ZERO, response.partsCost());
    }

    @Test
    void shouldThrowWhenWorkOrderDoesNotExist() {
        when(workOrderRepository.existsById(999L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> maintenanceCostService.getWorkOrderCost(999L)
        );

        verify(partUsageRepository, never())
                .findByWorkOrderId(anyLong());
    }

    @Test
    void shouldCalculateGeneralReport() {

        doReturn(1L).when(firstWorkOrder).getId();
        doReturn(2L).when(secondWorkOrder).getId();

        when(workOrderRepository.findAll())
                .thenReturn(List.of(firstWorkOrder, secondWorkOrder));

        when(partUsageRepository.findByWorkOrderId(1L))
                .thenReturn(List.of(
                        createPartUsage("Rolamento", 2, "25.50")
                ));

        when(partUsageRepository.findByWorkOrderId(2L))
                .thenReturn(List.of(
                        createPartUsage("Filtro", 1, "15.00")
                ));

        MaintenanceCostReportResponse response =
                maintenanceCostService.getReport();

        assertEquals(2, response.workOrders().size());
        assertEquals(
                0,
                new BigDecimal("66.00").compareTo(response.totalCost())
        );
        assertEquals(
                0,
                new BigDecimal("51.00").compareTo(
                        response.workOrders().get(0).partsCost()
                )
        );
        assertEquals(
                0,
                new BigDecimal("15.00").compareTo(
                        response.workOrders().get(1).partsCost()
                )
        );
    }

    @Test
    void shouldReturnZeroTotalWhenThereAreNoWorkOrders() {
        when(workOrderRepository.findAll()).thenReturn(List.of());

        MaintenanceCostReportResponse response =
                maintenanceCostService.getReport();

        assertTrue(response.workOrders().isEmpty());
        assertEquals(BigDecimal.ZERO, response.totalCost());
        verifyNoInteractions(partUsageRepository);
    }

    private PartUsage createPartUsage(
            String partName,
            int quantity,
            String unitCost
    ) {
        PartUsage partUsage = new PartUsage();
        partUsage.setPartName(partName);
        partUsage.setQuantity(quantity);
        partUsage.setUnitCost(new BigDecimal(unitCost));
        return partUsage;
    }
}
