
package br.com.industrialmaintenance.maintenancehistory;

import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordCreateRequest;
import br.com.industrialmaintenance.maintenancehistory.dto.MaintenanceRecordResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-records")
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    public MaintenanceRecordController(
            MaintenanceRecordService maintenanceRecordService
    ) {
        this.maintenanceRecordService = maintenanceRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceRecordResponse create(
            @Valid @RequestBody MaintenanceRecordCreateRequest request
    ) {
        return maintenanceRecordService.create(request);
    }

    @GetMapping("/work-order/{workOrderId}")
    public List<MaintenanceRecordResponse> findByWorkOrderId(
            @PathVariable Long workOrderId
    ) {
        return maintenanceRecordService.findByWorkOrderId(workOrderId);
    }
}
