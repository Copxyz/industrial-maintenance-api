
package br.com.industrialmaintenance.equipment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipments")

public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Equipment name is required")
    @Size(max = 120, message = "Equipment name must not exceed 120 characters")
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Equipment code is required")
    @Size(max = 50, message = "Equipment code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    @Column(length = 100)
    private String manufacturer;

    @Size(max = 100, message = "Model must not exceed 100 characters")
    @Column(length = 100)
    private String model;

    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Size(max = 120, message = "Location must not exceed 120 characters")
    @Column(length = 120)
    private String location;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    public Equipment() {
    }

    @PrePersist
    public void prePersist() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
}