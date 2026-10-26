
package br.com.industrialmaintenance.equipment;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(EquipmentController.class)
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EquipmentService equipmentService;

    @Test
    void findAllShouldReturnEquipmentList() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setName("Industrial Pump");
        equipment.setCode("EQP-001");

        when(equipmentService.findAll()).thenReturn(List.of(equipment));

        mockMvc.perform(get("/api/equipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Industrial Pump"))
                .andExpect(jsonPath("$[0].code").value("EQP-001"));
    }

    @Test
    void findByIdShouldReturnEquipmentWhenItExists() throws Exception {
        Equipment equipment = new Equipment();
        equipment.setName("Industrial Pump");
        equipment.setCode("EQP-001");

        when(equipmentService.findById(1L))
                .thenReturn(Optional.of(equipment));

        mockMvc.perform(get("/api/equipments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Industrial Pump"))
                .andExpect(jsonPath("$.code").value("EQP-001"));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenEquipmentDoesNotExist()
            throws Exception {

        when(equipmentService.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/equipments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturnCreatedWhenEquipmentIsValid()
            throws Exception {

        Equipment equipment = new Equipment();
        equipment.setName("Industrial Pump");
        equipment.setCode("EQP-001");

        when(equipmentService.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Industrial Pump"))
                .andExpect(jsonPath("$.code").value("EQP-001"));

        verify(equipmentService).save(any(Equipment.class));
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsMissing()
            throws Exception {

        String invalidJson = """
                {
                    "code": "EQP-002"
                }
                """;

        mockMvc.perform(post("/api/equipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturnOkWhenEquipmentExists() throws Exception {
        Equipment existingEquipment = new Equipment();
        existingEquipment.setName("Industrial Pump");
        existingEquipment.setCode("EQP-001");

        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setName("Updated Industrial Pump");
        updatedEquipment.setCode("EQP-001");

        when(equipmentService.findById(1L))
                .thenReturn(Optional.of(existingEquipment));

        when(equipmentService.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/equipments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEquipment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Industrial Pump"))
                .andExpect(jsonPath("$.code").value("EQP-001"));

        verify(equipmentService).save(any(Equipment.class));
    }

    @Test
    void updateShouldReturnNotFoundWhenEquipmentDoesNotExist()
            throws Exception {

        when(equipmentService.findById(999L))
                .thenReturn(Optional.empty());

        Equipment equipment = new Equipment();
        equipment.setName("Industrial Pump");
        equipment.setCode("EQP-999");

        mockMvc.perform(put("/api/equipments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenEquipmentExists() throws Exception {
        when(equipmentService.findById(1L))
                .thenReturn(Optional.of(new Equipment()));

        mockMvc.perform(delete("/api/equipments/1"))
                .andExpect(status().isNoContent());

        verify(equipmentService).deleteById(1L);
    }

    @Test
    void deleteShouldReturnNotFoundWhenEquipmentDoesNotExist()
            throws Exception {

        when(equipmentService.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/equipments/999"))
                .andExpect(status().isNotFound());
    }
}
