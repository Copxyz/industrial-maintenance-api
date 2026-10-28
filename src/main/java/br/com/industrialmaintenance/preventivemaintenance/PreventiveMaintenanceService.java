
package br.com.industrialmaintenance.preventivemaintenance;

import br.com.industrialmaintenance.equipment.Equipment;
import br.com.industrialmaintenance.equipment.EquipmentRepository;
import br.com.industrialmaintenance.exception.BusinessRuleException;
import br.com.industrialmaintenance.exception.ResourceNotFoundException;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceCreateRequest;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PreventiveMaintenanceService {

    private final PreventiveMaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;

    public PreventiveMaintenanceService(
            PreventiveMaintenanceRepository maintenanceRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public PreventiveMaintenanceResponse create(
            PreventiveMaintenanceCreateRequest request
    ) {
        Equipment equipment = equipmentRepository
                .findById(request.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: "
                                + request.equipmentId()
                ));

        if (request.scheduledDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(
                    "Scheduled date cannot be in the past"
            );
        }

        PreventiveMaintenance maintenance = new PreventiveMaintenance();
        maintenance.setEquipment(equipment);
        maintenance.setDescription(request.description());
        maintenance.setScheduledDate(request.scheduledDate());
        maintenance.setStatus(PreventiveMaintenanceStatus.PLANNED);

        PreventiveMaintenance savedMaintenance =
                maintenanceRepository.save(maintenance);

        return PreventiveMaintenanceResponse.fromEntity(savedMaintenance);
    }

    @Transactional(readOnly = true)
    public List<PreventiveMaintenanceResponse> findAll() {
        return maintenanceRepository.findAll()
                .stream()
                .map(PreventiveMaintenanceResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PreventiveMaintenanceResponse findById(Long id) {
        return PreventiveMaintenanceResponse.fromEntity(
                findMaintenanceById(id)
        );
    }

    @Transactional(readOnly = true)
    public List<PreventiveMaintenanceResponse> findOverdue() {
        return maintenanceRepository
                .findByStatusAndScheduledDateBefore(
                        PreventiveMaintenanceStatus.PLANNED,
                        LocalDate.now()
                )
                .stream()
                .map(PreventiveMaintenanceResponse::fromEntity)
                .toList();
    }

    @Transactional
    public PreventiveMaintenanceResponse complete(Long id) {
        PreventiveMaintenance maintenance = findMaintenanceById(id);

        if (maintenance.getStatus()
                != PreventiveMaintenanceStatus.PLANNED) {
            throw new BusinessRuleException(
                    "Only planned maintenance can be completed"
            );
        }

        maintenance.setStatus(PreventiveMaintenanceStatus.COMPLETED);
        maintenance.setCompletedAt(LocalDateTime.now());

        PreventiveMaintenance savedMaintenance =
                maintenanceRepository.save(maintenance);

        return PreventiveMaintenanceResponse.fromEntity(savedMaintenance);
    }

    @Transactional
    public PreventiveMaintenanceResponse cancel(Long id) {
        PreventiveMaintenance maintenance = findMaintenanceById(id);

        if (maintenance.getStatus()
                != PreventiveMaintenanceStatus.PLANNED) {
            throw new BusinessRuleException(
                    "Only planned maintenance can be cancelled"
            );
        }

        maintenance.setStatus(PreventiveMaintenanceStatus.CANCELLED);

        PreventiveMaintenance savedMaintenance =
                maintenanceRepository.save(maintenance);

        return PreventiveMaintenanceResponse.fromEntity(savedMaintenance);
    }

    private PreventiveMaintenance findMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Preventive maintenance not found with id: " + id
                ));
    }
}
