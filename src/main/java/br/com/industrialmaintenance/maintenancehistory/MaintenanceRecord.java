
package br.com.industrialmaintenance.maintenancehistory;

import br.com.industrialmaintenance.workorder.WorkOrder;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "failure_description", nullable = false, length = 1000)
    private String failureDescription;

    @Column(name = "solution_applied", nullable = false, length = 1000)
    private String solutionApplied;

    @Column(length = 2000)
    private String notes;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    public MaintenanceRecord() {
    }

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public String getFailureDescription() {
        return failureDescription;
    }

    public void setFailureDescription(String failureDescription) {
        this.failureDescription = failureDescription;
    }

    public String getSolutionApplied() {
        return solutionApplied;
    }

    public void setSolutionApplied(String solutionApplied) {
        this.solutionApplied = solutionApplied;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
