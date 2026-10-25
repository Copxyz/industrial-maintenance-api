
package br.com.industrialmaintenance.equipment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EquipmentServiceTest {

    private EquipmentRepository equipmentRepository;
    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        equipmentRepository = mock(EquipmentRepository.class);
        equipmentService = new EquipmentService(equipmentRepository);
    }

    @Test
    void findAllShouldReturnAllEquipments() {
        List<Equipment> equipments = List.of(
                new Equipment(),
                new Equipment()
        );

        when(equipmentRepository.findAll()).thenReturn(equipments);

        List<Equipment> result = equipmentService.findAll();

        assertEquals(2, result.size());
        assertSame(equipments, result);
        verify(equipmentRepository).findAll();
    }

    @Test
    void findByIdShouldReturnEquipmentWhenItExists() {
        Long id = 1L;
        Equipment equipment = new Equipment();

        when(equipmentRepository.findById(id))
                .thenReturn(Optional.of(equipment));

        Optional<Equipment> result = equipmentService.findById(id);

        assertTrue(result.isPresent());
        assertSame(equipment, result.get());
        verify(equipmentRepository).findById(id);
    }

    @Test
    void saveShouldReturnSavedEquipment() {
        Equipment equipment = new Equipment();

        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        Equipment result = equipmentService.save(equipment);

        assertSame(equipment, result);
        verify(equipmentRepository).save(equipment);
    }

    @Test
    void deleteByIdShouldCallRepository() {
        Long id = 1L;

        equipmentService.deleteById(id);

        verify(equipmentRepository).deleteById(id);
    }
}
