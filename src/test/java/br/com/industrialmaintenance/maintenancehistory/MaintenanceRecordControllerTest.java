
package br.com.industrialmaintenance.maintenancehistory;

import br.com.industrialmaintenance.exception.GlobalExceptionHandler;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordCreateRequest;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordResponse;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceRecordController.class)
@Import(GlobalExceptionHandler.class)
class MaintenanceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaintenanceRecordService maintenanceRecordService;

    @Test
    void shouldCreateMaintenanceRecord() throws Exception {
        MaintenanceRecordCreateRequest request =
                new MaintenanceRecordCreateRequest(
                        1L,
                        "Motor superaquecendo",
                        "Limpeza do sistema de ventilação",
                        "Temperatura normalizada"
                );

        MaintenanceRecordResponse response =
                new MaintenanceRecordResponse(
                        10L,
                        1L,
                        "Motor superaquecendo",
                        "Limpeza do sistema de ventilação",
                        "Temperatura normalizada",
                        LocalDateTime.now()
                );

        when(maintenanceRecordService.create(any(
                MaintenanceRecordCreateRequest.class
        ))).thenReturn(response);

        mockMvc.perform(post("/api/maintenance-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.workOrderId").value(1))
                .andExpect(jsonPath("$.failureDescription")
                        .value("Motor superaquecendo"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidRecord() throws Exception {
        MaintenanceRecordCreateRequest request =
                new MaintenanceRecordCreateRequest(
                        null,
                        "",
                        "",
                        null
                );

        mockMvc.perform(post("/api/maintenance-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(maintenanceRecordService, never()).create(any());
    }

    @Test
    void shouldReturnRecordsForWorkOrder() throws Exception {
        MaintenanceRecordResponse response =
                new MaintenanceRecordResponse(
                        10L,
                        1L,
                        "Vibração excessiva",
                        "Ajuste do equipamento",
                        null,
                        LocalDateTime.now()
                );

        when(maintenanceRecordService.findByWorkOrderId(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get(
                        "/api/maintenance-records/work-order/1"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].workOrderId").value(1))
                .andExpect(jsonPath("$[0].failureDescription")
                        .value("Vibração excessiva"));
    }

    @Test
    void shouldReturnNotFoundWhenWorkOrderDoesNotExist() throws Exception {
        when(maintenanceRecordService.findByWorkOrderId(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(get(
                        "/api/maintenance-records/work-order/999"
                ))
                .andExpect(status().isNotFound());
    }
}
