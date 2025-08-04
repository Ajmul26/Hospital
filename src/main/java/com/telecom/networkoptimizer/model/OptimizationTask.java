package com.telecom.networkoptimizer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "optimization_tasks")
public class OptimizationTask {
    
    @Id
    private String id;
    
    @NotBlank(message = "Task name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Task type is required")
    private TaskType type;
    
    @NotNull(message = "Task status is required")
    private TaskStatus status;
    
    @NotNull(message = "Priority is required")
    private Priority priority;
    
    @Indexed
    private String initiatedBy;
    
    private List<String> targetNodeIds;
    
    private Map<String, Object> parameters;
    
    private Map<String, Object> results;
    
    private String algorithm;
    
    private Integer maxIterations;
    
    private Double convergenceThreshold;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer progressPercentage;
    
    private String errorMessage;
    
    private Double improvementPercentage;
    
    private Map<String, Double> performanceGains;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum TaskType {
        LOAD_BALANCING,
        BANDWIDTH_OPTIMIZATION,
        ROUTE_OPTIMIZATION,
        CAPACITY_PLANNING,
        FAULT_DETECTION,
        PERFORMANCE_TUNING,
        COST_OPTIMIZATION,
        ENERGY_EFFICIENCY
    }
    
    public enum TaskStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        PAUSED
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    public Long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
    
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }
    
    public boolean isRunning() {
        return status == TaskStatus.RUNNING;
    }
    
    public boolean hasFailed() {
        return status == TaskStatus.FAILED;
    }
}