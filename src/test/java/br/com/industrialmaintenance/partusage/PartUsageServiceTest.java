
package br.com.industrialmaintenance.partusage;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.partusage.dto.PartUsageCreateRequest;
import br.com.industrialmaintenance.partusage.dto.PartUsageResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartUsageServiceTest {

    @Mock
    private PartUsageRepository partUsageRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private PartUsageService partUsageService;

    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        workOrder = spy(new WorkOrder());
    }

    @Test
    void shouldCreatePartUsageAndCalculateTotalCost() {
        PartUsageCreateRequest request = new PartUsageCreateRequest(
                1L,
                "Rolamento industrial",
                3,
                new BigDecimal("25.50")
        );

        doReturn(1L).when(workOrder).getId();

        when(workOrderRepository.findById(1L))
                .thenReturn(Optional.of(workOrder));

        when(partUsageRepository.save(any(PartUsage.class)))
                .thenAnswer(invocation -> {
                    PartUsage partUsage = invocation.getArgument(0);

                    PartUsage savedPartUsage = spy(partUsage);
                    doReturn(10L).when(savedPartUsage).getId();

                    return savedPartUsage;
                });

        PartUsageResponse response = partUsageService.create(request);

        assertEquals(10L, response.id());
        assertEquals(1L, response.workOrderId());
        assertEquals("Rolamento industrial", response.partName());
        assertEquals(3, response.quantity());
        assertEquals(0, new BigDecimal("25.50").compareTo(response.unitCost()));
        assertEquals(0, new BigDecimal("76.50").compareTo(response.totalCost()));

        verify(partUsageRepository).save(any(PartUsage.class));
    }

    @Test
    void shouldThrowWhenCreatingUsageForMissingWorkOrder() {
        PartUsageCreateRequest request = new PartUsageCreateRequest(
                999L,
                "Rolamento industrial",
                1,
                new BigDecimal("25.50")
        );

        when(workOrderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> partUsageService.create(request)
        );

        verify(partUsageRepository, never()).save(any());
    }

    @Test
    void shouldReturnPartUsagesForWorkOrder() {
        PartUsage partUsage = new PartUsage();
        partUsage.setWorkOrder(workOrder);
        partUsage.setPartName("Filtro industrial");
        partUsage.setQuantity(2);
        partUsage.setUnitCost(new BigDecimal("15.00"));

        doReturn(1L).when(workOrder).getId();

        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(partUsageRepository.findByWorkOrderId(1L))
                .thenReturn(List.of(partUsage));

        List<PartUsageResponse> responses =
                partUsageService.findByWorkOrderId(1L);

        assertEquals(1, responses.size());
        assertEquals("Filtro industrial", responses.get(0).partName());
        assertEquals(0, new BigDecimal("30.00")
                .compareTo(responses.get(0).totalCost()));
    }

    @Test
    void shouldReturnEmptyListWhenWorkOrderHasNoPartUsages() {
        when(workOrderRepository.existsById(1L)).thenReturn(true);
        when(partUsageRepository.findByWorkOrderId(1L))
                .thenReturn(List.of());

        List<PartUsageResponse> responses =
                partUsageService.findByWorkOrderId(1L);

        assertTrue(responses.isEmpty());
    }

    @Test
    void shouldThrowWhenSearchingUsagesForMissingWorkOrder() {
        when(workOrderRepository.existsById(999L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> partUsageService.findByWorkOrderId(999L)
        );

        verify(partUsageRepository, never()).findByWorkOrderId(any());
    }
}
