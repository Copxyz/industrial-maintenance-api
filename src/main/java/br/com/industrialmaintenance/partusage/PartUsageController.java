
package br.com.industrialmaintenance.partusage;

import br.com.industrialmaintenance.partusage.dto.PartUsageCreateRequest;
import br.com.industrialmaintenance.partusage.dto.PartUsageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/part-usages")
public class PartUsageController {

    private final PartUsageService partUsageService;

    public PartUsageController(PartUsageService partUsageService) {
        this.partUsageService = partUsageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartUsageResponse create(
            @Valid @RequestBody PartUsageCreateRequest request
    ) {
        return partUsageService.create(request);
    }

    @GetMapping("/work-order/{workOrderId}")
    public List<PartUsageResponse> findByWorkOrderId(
            @PathVariable Long workOrderId
    ) {
        return partUsageService.findByWorkOrderId(workOrderId);
    }
}
