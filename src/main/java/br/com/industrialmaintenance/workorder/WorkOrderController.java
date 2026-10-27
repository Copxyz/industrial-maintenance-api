
package br.com.industrialmaintenance.workorder;

import br.com.industrialmaintenance.workorder.dto.WorkOrderCreateRequest;
import br.com.industrialmaintenance.workorder.dto.WorkOrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrderResponse create(
            @Valid @RequestBody WorkOrderCreateRequest request) {
        return workOrderService.create(
                request.equipmentId(),
                request.description()
        );
    }

    @GetMapping
    public List<WorkOrderResponse> findAll() {
        return workOrderService.findAll();
    }

    @GetMapping("/{id}")
    public WorkOrderResponse findById(@PathVariable Long id) {
        return workOrderService.findById(id);
    }

    @GetMapping("/status/{status}")
    public List<WorkOrderResponse> findByStatus(
            @PathVariable WorkOrderStatus status) {
        return workOrderService.findByStatus(status);
    }

    @GetMapping("/equipment/{equipmentId}")
    public List<WorkOrderResponse> findByEquipmentId(
            @PathVariable Long equipmentId) {
        return workOrderService.findByEquipmentId(equipmentId);
    }

    @PatchMapping("/{id}/start")
    public WorkOrderResponse start(@PathVariable Long id) {
        return workOrderService.start(id);
    }

    @PatchMapping("/{id}/complete")
    public WorkOrderResponse complete(@PathVariable Long id) {
        return workOrderService.complete(id);
    }
}
