
package br.com.industrialmaintenance.workorder;

import br.com.industrialmaintenance.exception.BusinessRuleException;
import br.com.industrialmaintenance.exception.GlobalExceptionHandler;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.workorder.dto.WorkOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkOrderController.class)
@Import(GlobalExceptionHandler.class)
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkOrderService workOrderService;

    @Test
    void shouldCreateWorkOrder() throws Exception {
        when(workOrderService.create(1L, "Trocar rolamento"))
                .thenReturn(createResponse(
                        10L,
                        1L,
                        "Trocar rolamento",
                        WorkOrderStatus.OPEN
                ));

        mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "equipmentId": 1,
                                    "description": "Trocar rolamento"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.equipmentId").value(1))
                .andExpect(jsonPath("$.description")
                        .value("Trocar rolamento"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWorkOrderWithInvalidData()
            throws Exception {
        mockMvc.perform(post("/api/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "equipmentId": 1,
                                    "description": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllWorkOrders() throws Exception {
        when(workOrderService.findAll()).thenReturn(List.of(
                createResponse(
                        10L, 1L, "Trocar rolamento", WorkOrderStatus.OPEN
                ),
                createResponse(
                        11L, 2L, "Inspecionar motor", WorkOrderStatus.IN_PROGRESS
                )
        ));

        mockMvc.perform(get("/api/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(11));
    }

    @Test
    void shouldReturnWorkOrderById() throws Exception {
        when(workOrderService.findById(10L)).thenReturn(
                createResponse(
                        10L, 1L, "Trocar rolamento", WorkOrderStatus.OPEN
                )
        );

        mockMvc.perform(get("/api/work-orders/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.equipmentId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldReturnNotFoundWhenWorkOrderDoesNotExist()
            throws Exception {
        when(workOrderService.findById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(get("/api/work-orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnWorkOrdersByStatus() throws Exception {
        when(workOrderService.findByStatus(WorkOrderStatus.OPEN))
                .thenReturn(List.of(
                        createResponse(
                                10L, 1L, "Trocar rolamento",
                                WorkOrderStatus.OPEN
                        )
                ));

        mockMvc.perform(get("/api/work-orders/status/OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void shouldReturnWorkOrdersByEquipmentId() throws Exception {
        when(workOrderService.findByEquipmentId(1L))
                .thenReturn(List.of(
                        createResponse(
                                10L, 1L, "Trocar rolamento",
                                WorkOrderStatus.OPEN
                        )
                ));

        mockMvc.perform(get("/api/work-orders/equipment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].equipmentId").value(1));
    }

    @Test
    void shouldStartWorkOrder() throws Exception {
        when(workOrderService.start(10L)).thenReturn(
                createResponse(
                        10L, 1L, "Trocar rolamento",
                        WorkOrderStatus.IN_PROGRESS
                )
        );

        mockMvc.perform(patch("/api/work-orders/10/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldCompleteWorkOrder() throws Exception {
        when(workOrderService.complete(10L)).thenReturn(
                createResponse(
                        10L, 1L, "Trocar rolamento",
                        WorkOrderStatus.COMPLETED
                )
        );

        mockMvc.perform(patch("/api/work-orders/10/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    @Test
    void shouldReturnConflictWhenBusinessRulePreventsCompletion()
            throws Exception {
        when(workOrderService.complete(10L))
                .thenThrow(new BusinessRuleException(
                        "Only in-progress work orders can be completed"
                ));

        mockMvc.perform(patch("/api/work-orders/10/complete"))
                .andExpect(status().isConflict());
    }

    private WorkOrderResponse createResponse(
            Long id,
            Long equipmentId,
            String description,
            WorkOrderStatus status
    ) {
        LocalDateTime openedAt = LocalDateTime.of(
                2026, 10, 29, 10, 0
        );

        LocalDateTime completedAt =
                status == WorkOrderStatus.COMPLETED
                        ? openedAt.plusHours(1)
                        : null;

        return new WorkOrderResponse(
                id,
                equipmentId,
                description,
                status,
                openedAt,
                completedAt
        );
    }
}
