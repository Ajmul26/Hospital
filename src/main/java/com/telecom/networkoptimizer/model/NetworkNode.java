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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "network_nodes")
public class NetworkNode {
    
    @Id
    private String id;
    
    @NotBlank(message = "Node name is required")
    @Indexed(unique = true)
    private String name;
    
    @NotNull(message = "Node type is required")
    private NodeType type;
    
    @NotNull(message = "Location is required")
    private Location location;
    
    @NotNull(message = "Status is required")
    private NodeStatus status;
    
    @Min(value = 0, message = "Capacity must be non-negative")
    private Double capacity;
    
    @Min(value = 0, message = "Current load must be non-negative")
    @Max(value = 100, message = "Current load cannot exceed 100%")
    private Double currentLoad;
    
    @Min(value = 0, message = "Available bandwidth must be non-negative")
    private Double availableBandwidth;
    
    @Min(value = 0, message = "Used bandwidth must be non-negative")
    private Double usedBandwidth;
    
    private List<String> connectedNodes;
    
    private Map<String, Object> performanceMetrics;
    
    private Map<String, Object> configuration;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum NodeType {
        BASE_STATION,
        ROUTER,
        SWITCH,
        GATEWAY,
        REPEATER,
        ACCESS_POINT,
        CORE_SWITCH,
        EDGE_ROUTER
    }
    
    public enum NodeStatus {
        ACTIVE,
        INACTIVE,
        MAINTENANCE,
        ERROR,
        OVERLOADED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
        private String city;
        private String region;
        private String country;
    }
    
    public Double getUtilizationPercentage() {
        if (capacity == null || capacity == 0) {
            return 0.0;
        }
        return (currentLoad / capacity) * 100;
    }
    
    public Double getBandwidthUtilizationPercentage() {
        if (availableBandwidth == null || availableBandwidth == 0) {
            return 0.0;
        }
        return (usedBandwidth / (availableBandwidth + usedBandwidth)) * 100;
    }
    
    public boolean isOverloaded() {
        return getUtilizationPercentage() > 80.0;
    }
}