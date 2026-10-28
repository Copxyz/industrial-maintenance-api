
package br.com.industrialmaintenance.equipment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.industrialmaintenance.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> findAll() {
        return equipmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> findById(@PathVariable Long id) {
        Equipment equipment = equipmentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id
                ));

        return ResponseEntity.ok(equipment);
    }

    @PostMapping
    public ResponseEntity<Equipment> create(@Valid @RequestBody Equipment equipment) {
        Equipment savedEquipment = equipmentService.save(equipment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedEquipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> update(
            @PathVariable Long id,
            @Valid @RequestBody Equipment equipment) {

        Equipment existingEquipment = equipmentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id
                ));

        existingEquipment.setName(equipment.getName());
        existingEquipment.setCode(equipment.getCode());
        existingEquipment.setManufacturer(equipment.getManufacturer());
        existingEquipment.setModel(equipment.getModel());
        existingEquipment.setSerialNumber(equipment.getSerialNumber());
        existingEquipment.setLocation(equipment.getLocation());

        Equipment updatedEquipment = equipmentService.save(existingEquipment);

        return ResponseEntity.ok(updatedEquipment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        equipmentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id
                ));

        equipmentService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
