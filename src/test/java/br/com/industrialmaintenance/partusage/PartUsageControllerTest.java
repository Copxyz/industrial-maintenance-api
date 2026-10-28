
package br.com.industrialmaintenance.partusage;

import br.com.industrialmaintenance.exception.GlobalExceptionHandler;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.partusage.dto.PartUsageCreateRequest;
import br.com.industrialmaintenance.partusage.dto.PartUsageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartUsageController.class)
@Import(GlobalExceptionHandler.class)
class PartUsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PartUsageService partUsageService;

    @Test
    void shouldCreatePartUsage() throws Exception {
        PartUsageCreateRequest request = new PartUsageCreateRequest(
                1L,
                "Rolamento industrial",
                3,
                new BigDecimal("25.50")
        );

        PartUsageResponse response = new PartUsageResponse(
                10L,
                1L,
                "Rolamento industrial",
                3,
                new BigDecimal("25.50"),
                new BigDecimal("76.50"),
                LocalDateTime.now()
        );

        when(partUsageService.create(any(PartUsageCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/part-usages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.workOrderId").value(1))
                .andExpect(jsonPath("$.partName")
                        .value("Rolamento industrial"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.totalCost").value(76.50));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidUsage() throws Exception {
        PartUsageCreateRequest request = new PartUsageCreateRequest(
                null,
                "",
                0,
                new BigDecimal("-1.00")
        );

        mockMvc.perform(post("/api/part-usages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(partUsageService, never()).create(any());
    }

    @Test
    void shouldReturnPartUsagesForWorkOrder() throws Exception {
        PartUsageResponse response = new PartUsageResponse(
                10L,
                1L,
                "Filtro industrial",
                2,
                new BigDecimal("15.00"),
                new BigDecimal("30.00"),
                LocalDateTime.now()
        );

        when(partUsageService.findByWorkOrderId(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/part-usages/work-order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].partName")
                        .value("Filtro industrial"))
                .andExpect(jsonPath("$[0].totalCost").value(30.00));
    }

    @Test
    void shouldReturnNotFoundWhenWorkOrderDoesNotExist() throws Exception {
        when(partUsageService.findByWorkOrderId(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Work order not found with id: 999"
                ));

        mockMvc.perform(get("/api/part-usages/work-order/999"))
                .andExpect(status().isNotFound());
    }
}
