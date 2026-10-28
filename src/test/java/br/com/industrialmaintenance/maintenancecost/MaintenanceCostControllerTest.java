
package br.com.industrialmaintenance.maintenancecost;

import br.com.industrialmaintenance.exception.GlobalExceptionHandler;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.maintenancecost.dto.MaintenanceCostReportResponse;
import br.com.industrialmaintenance.maintenancecost.dto.WorkOrderCostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaintenanceCostController.class)
@Import(GlobalExceptionHandler.class)
class MaintenanceCostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceCostService maintenanceCostService;

    @Test
    void shouldReturnGeneralMaintenanceCostReport() throws Exception {
        when(maintenanceCostService.getReport()).thenReturn(
                new MaintenanceCostReportResponse(
                        new BigDecimal("66.00"),
                        List.of(
                                new WorkOrderCostResponse(
                                        1L,
                                        new BigDecimal("51.00")
                                ),
                                new WorkOrderCostResponse(
                                        2L,
                                        new BigDecimal("15.00")
                                )
                        )
                )
        );

        mockMvc.perform(get("/api/maintenance-costs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCost").value(66.00))
                .andExpect(jsonPath("$.workOrders.length()").value(2))
                .andExpect(jsonPath("$.workOrders[0].workOrderId").value(1))
                .andExpect(jsonPath("$.workOrders[0].partsCost").value(51.00))
                .andExpect(jsonPath("$.workOrders[1].workOrderId").value(2))
                .andExpect(jsonPath("$.workOrders[1].partsCost").value(15.00));
    }

    @Test
    void shouldReturnCostForSpecificWorkOrder() throws Exception {
        when(maintenanceCostService.getWorkOrderCost(1L)).thenReturn(
                new WorkOrderCostResponse(
                        1L,
                        new BigDecimal("51.00")
                )
        );

        mockMvc.perform(get("/api/maintenance-costs/work-order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workOrderId").value(1))
                .andExpect(jsonPath("$.partsCost").value(51.00));
    }

    @Test
    void shouldReturnNotFoundWhenWorkOrderDoesNotExist() throws Exception {
        when(maintenanceCostService.getWorkOrderCost(999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Work order not found with id: 999"
                        )
                );

        mockMvc.perform(get("/api/maintenance-costs/work-order/999"))
                .andExpect(status().isNotFound());
    }
}
