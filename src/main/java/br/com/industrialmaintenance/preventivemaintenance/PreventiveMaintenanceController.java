
package br.com.industrialmaintenance.preventivemaintenance;

import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceCreateRequest;
import br.com.industrialmaintenance.preventivemaintenance.dto.PreventiveMaintenanceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preventive-maintenances")
public class PreventiveMaintenanceController {

    private final PreventiveMaintenanceService maintenanceService;

    public PreventiveMaintenanceController(
            PreventiveMaintenanceService maintenanceService
    ) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreventiveMaintenanceResponse create(
            @Valid @RequestBody PreventiveMaintenanceCreateRequest request
    ) {
        return maintenanceService.create(request);
    }

    @GetMapping
    public List<PreventiveMaintenanceResponse> findAll() {
        return maintenanceService.findAll();
    }

    @GetMapping("/{id}")
    public PreventiveMaintenanceResponse findById(@PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @GetMapping("/overdue")
    public List<PreventiveMaintenanceResponse> findOverdue() {
        return maintenanceService.findOverdue();
    }

    @PatchMapping("/{id}/complete")
    public PreventiveMaintenanceResponse complete(@PathVariable Long id) {
        return maintenanceService.complete(id);
    }

    @PatchMapping("/{id}/cancel")
    public PreventiveMaintenanceResponse cancel(@PathVariable Long id) {
        return maintenanceService.cancel(id);
    }
}
