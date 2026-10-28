
package br.com.industrialmaintenance.preventivemaintenance;

import tools.jackson.databind.ObjectMapper;

import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceCreateRequest;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceResponse;

//import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreventiveMaintenanceController.class)
class PreventiveMaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PreventiveMaintenanceService maintenanceService;

    private PreventiveMaintenanceResponse createResponse(Long id) {
        return new PreventiveMaintenanceResponse(
                id,
                1L,
                "Inspect pump",
                LocalDate.now().plusDays(7),
                PreventiveMaintenanceStatus.PLANNED,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void shouldCreateMaintenanceSuccessfully() throws Exception {
        PreventiveMaintenanceCreateRequest request =
                new PreventiveMaintenanceCreateRequest(
                        1L,
                        "Inspect pump",
                        LocalDate.now().plusDays(7)
                );

        when(maintenanceService.create(any(PreventiveMaintenanceCreateRequest.class)))
                .thenReturn(createResponse(1L));

        mockMvc.perform(post("/api/preventive-maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.equipmentId").value(1))
                .andExpect(jsonPath("$.description").value("Inspect pump"))
                .andExpect(jsonPath("$.status").value("PLANNED"));

        verify(maintenanceService).create(any(PreventiveMaintenanceCreateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenEquipmentIdIsMissing() throws Exception {
        String request = """
                {
                    "description": "Inspect pump",
                    "scheduledDate": "2030-05-20"
                }
                """;

        mockMvc.perform(post("/api/preventive-maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(maintenanceService);
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionIsBlank() throws Exception {
        PreventiveMaintenanceCreateRequest request =
                new PreventiveMaintenanceCreateRequest(
                        1L,
                        "   ",
                        LocalDate.now().plusDays(7)
                );

        mockMvc.perform(post("/api/preventive-maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(maintenanceService);
    }

    @Test
    void shouldReturnBadRequestWhenScheduledDateIsMissing() throws Exception {
        String request = """
                {
                    "equipmentId": 1,
                    "description": "Inspect pump"
                }
                """;

        mockMvc.perform(post("/api/preventive-maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(maintenanceService);
    }

    @Test
    void shouldReturnAllMaintenances() throws Exception {
        when(maintenanceService.findAll())
                .thenReturn(List.of(createResponse(1L), createResponse(2L)));

        mockMvc.perform(get("/api/preventive-maintenances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(maintenanceService).findAll();
    }

    @Test
    void shouldReturnMaintenanceById() throws Exception {
        when(maintenanceService.findById(1L)).thenReturn(createResponse(1L));

        mockMvc.perform(get("/api/preventive-maintenances/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Inspect pump"));

        verify(maintenanceService).findById(1L);
    }

    @Test
    void shouldReturnOverdueMaintenances() throws Exception {
        when(maintenanceService.findOverdue())
                .thenReturn(List.of(createResponse(1L)));

        mockMvc.perform(get("/api/preventive-maintenances/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(maintenanceService).findOverdue();
    }

    @Test
    void shouldCompleteMaintenance() throws Exception {
        PreventiveMaintenanceResponse response = new PreventiveMaintenanceResponse(
                1L,
                1L,
                "Inspect pump",
                LocalDate.now().plusDays(7),
                PreventiveMaintenanceStatus.COMPLETED,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        when(maintenanceService.complete(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/preventive-maintenances/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());

        verify(maintenanceService).complete(1L);
    }

    @Test
    void shouldCancelMaintenance() throws Exception {
        PreventiveMaintenanceResponse response = new PreventiveMaintenanceResponse(
                1L,
                1L,
                "Inspect pump",
                LocalDate.now().plusDays(7),
                PreventiveMaintenanceStatus.CANCELLED,
                LocalDateTime.now(),
                null
        );

        when(maintenanceService.cancel(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/preventive-maintenances/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(maintenanceService).cancel(1L);
    }
}
