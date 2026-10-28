package br.com.industrialmaintenance.preventivemaintenance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PreventiveMaintenanceRepository
        extends JpaRepository<PreventiveMaintenance, Long> {

    List<PreventiveMaintenance> findByStatus(
            PreventiveMaintenanceStatus status
    );

    List<PreventiveMaintenance> findByStatusAndScheduledDateBefore(
            PreventiveMaintenanceStatus status,
            LocalDate date
    );
}