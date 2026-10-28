
package br.com.industrialmaintenance.preventivemaintenance;

import br.com.industrialmaintenance.equipment.Equipment;
import br.com.industrialmaintenance.equipment.EquipmentRepository;
import br.com.industrialmaintenance.exception.BusinessRuleException;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceCreateRequest;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreventiveMaintenanceServiceTest {

    @Mock
    private PreventiveMaintenanceRepository maintenanceRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private PreventiveMaintenanceService maintenanceService;

    private Equipment equipment;

    @BeforeEach
    void setUp() {
        equipment = mock(Equipment.class);
    }

    @Test
    void shouldCreateMaintenanceSuccessfully() {

        when(equipment.getId()).thenReturn(1L);

        LocalDate scheduledDate = LocalDate.now().plusDays(7);

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(maintenanceRepository.save(any(PreventiveMaintenance.class)))
                .thenAnswer(invocation -> {
                    PreventiveMaintenance maintenance =
                            invocation.getArgument(0);
                    maintenance.setCompletedAt(null);
                    return maintenance;
                });

        PreventiveMaintenanceResponse response = maintenanceService.create(
                new PreventiveMaintenanceCreateRequest(
                        1L, "Inspect pump", scheduledDate
                )
        );

        assertEquals(1L, response.equipmentId());
        assertEquals("Inspect pump", response.description());
        assertEquals(scheduledDate, response.scheduledDate());
        assertEquals(PreventiveMaintenanceStatus.PLANNED, response.status());
        assertNull(response.completedAt());

        verify(maintenanceRepository).save(any(PreventiveMaintenance.class));
    }

    @Test
    void shouldThrowWhenEquipmentDoesNotExist() {
        when(equipmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> maintenanceService.create(
                        new PreventiveMaintenanceCreateRequest(
                                999L,
                                "Inspect pump",
                                LocalDate.now().plusDays(7)
                        )
                ));

        verify(maintenanceRepository, never())
                .save(any(PreventiveMaintenance.class));
    }

    @Test
    void shouldThrowWhenScheduledDateIsInThePast() {
        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        assertThrows(BusinessRuleException.class,
                () -> maintenanceService.create(
                        new PreventiveMaintenanceCreateRequest(
                                1L,
                                "Inspect pump",
                                LocalDate.now().minusDays(1)
                        )
                ));

        verify(maintenanceRepository, never())
                .save(any(PreventiveMaintenance.class));
    }

    @Test
    void shouldFindAllMaintenances() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.PLANNED,
                LocalDate.now().plusDays(3)
        );

        when(maintenanceRepository.findAll())
                .thenReturn(List.of(maintenance));

        List<PreventiveMaintenanceResponse> result =
                maintenanceService.findAll();

        assertEquals(1, result.size());
        assertEquals("Inspect pump", result.get(0).description());
    }

    @Test
    void shouldFindMaintenanceById() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.PLANNED,
                LocalDate.now().plusDays(3)
        );

        when(maintenanceRepository.findById(1L))
                .thenReturn(Optional.of(maintenance));

        PreventiveMaintenanceResponse response =
                maintenanceService.findById(1L);

        assertEquals("Inspect pump", response.description());
        assertEquals(PreventiveMaintenanceStatus.PLANNED, response.status());
    }

    @Test
    void shouldThrowWhenMaintenanceDoesNotExist() {
        when(maintenanceRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> maintenanceService.findById(999L));
    }

    @Test
    void shouldFindOverdueMaintenances() {
        PreventiveMaintenance overdue = createMaintenance(
                PreventiveMaintenanceStatus.PLANNED,
                LocalDate.now().minusDays(1)
        );

        when(maintenanceRepository.findByStatusAndScheduledDateBefore(
                eq(PreventiveMaintenanceStatus.PLANNED),
                any(LocalDate.class)
        )).thenReturn(List.of(overdue));

        List<PreventiveMaintenanceResponse> result =
                maintenanceService.findOverdue();

        assertEquals(1, result.size());
        assertEquals(PreventiveMaintenanceStatus.PLANNED,
                result.get(0).status());

        verify(maintenanceRepository).findByStatusAndScheduledDateBefore(
                eq(PreventiveMaintenanceStatus.PLANNED),
                eq(LocalDate.now())
        );
    }

    @Test
    void shouldCompletePlannedMaintenance() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.PLANNED,
                LocalDate.now()
        );

        when(maintenanceRepository.findById(1L))
                .thenReturn(Optional.of(maintenance));

        when(maintenanceRepository.save(any(PreventiveMaintenance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PreventiveMaintenanceResponse response =
                maintenanceService.complete(1L);

        assertEquals(PreventiveMaintenanceStatus.COMPLETED, response.status());
        assertNotNull(response.completedAt());
    }

    @Test
    void shouldThrowWhenCompletingNonPlannedMaintenance() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.COMPLETED,
                LocalDate.now()
        );

        when(maintenanceRepository.findById(1L))
                .thenReturn(Optional.of(maintenance));

        assertThrows(BusinessRuleException.class,
                () -> maintenanceService.complete(1L));

        verify(maintenanceRepository, never())
                .save(any(PreventiveMaintenance.class));
    }

    @Test
    void shouldCancelPlannedMaintenance() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.PLANNED,
                LocalDate.now().plusDays(3)
        );

        when(maintenanceRepository.findById(1L))
                .thenReturn(Optional.of(maintenance));

        when(maintenanceRepository.save(any(PreventiveMaintenance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PreventiveMaintenanceResponse response =
                maintenanceService.cancel(1L);

        assertEquals(PreventiveMaintenanceStatus.CANCELLED, response.status());
        assertNull(response.completedAt());
    }

    @Test
    void shouldThrowWhenCancellingNonPlannedMaintenance() {
        PreventiveMaintenance maintenance = createMaintenance(
                PreventiveMaintenanceStatus.CANCELLED,
                LocalDate.now().plusDays(3)
        );

        when(maintenanceRepository.findById(1L))
                .thenReturn(Optional.of(maintenance));

        assertThrows(BusinessRuleException.class,
                () -> maintenanceService.cancel(1L));

        verify(maintenanceRepository, never())
                .save(any(PreventiveMaintenance.class));
    }

    private PreventiveMaintenance createMaintenance(
            PreventiveMaintenanceStatus status,
            LocalDate scheduledDate
    ) {
        PreventiveMaintenance maintenance = new PreventiveMaintenance();
        maintenance.setEquipment(equipment);
        maintenance.setDescription("Inspect pump");
        maintenance.setScheduledDate(scheduledDate);
        maintenance.setStatus(status);

        return maintenance;
    }
}
