package com.telecom.networkoptimizer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "network_metrics")
public class NetworkMetrics {
    
    @Id
    private String id;
    
    @NotNull(message = "Node ID is required")
    @Indexed
    private String nodeId;
    
    @NotNull(message = "Timestamp is required")
    @Indexed
    private LocalDateTime timestamp;
    
    @Min(value = 0, message = "Latency must be non-negative")
    private Double latency; // milliseconds
    
    @Min(value = 0, message = "Throughput must be non-negative")
    private Double throughput; // Mbps
    
    @Min(value = 0, message = "Packet loss must be non-negative")
    private Double packetLoss; // percentage
    
    @Min(value = 0, message = "Jitter must be non-negative")
    private Double jitter; // milliseconds
    
    @Min(value = 0, message = "CPU utilization must be non-negative")
    private Double cpuUtilization; // percentage
    
    @Min(value = 0, message = "Memory utilization must be non-negative")
    private Double memoryUtilization; // percentage
    
    @Min(value = 0, message = "Bandwidth utilization must be non-negative")
    private Double bandwidthUtilization; // percentage
    
    @Min(value = 0, message = "Active connections must be non-negative")
    private Integer activeConnections;
    
    @Min(value = 0, message = "Error count must be non-negative")
    private Integer errorCount;
    
    private Double signalStrength; // dBm for wireless nodes
    
    private Double temperature; // Celsius
    
    private Double powerConsumption; // Watts
    
    private Map<String, Object> customMetrics;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public QualityOfService getQualityOfService() {
        if (latency == null || throughput == null || packetLoss == null) {
            return QualityOfService.UNKNOWN;
        }
        
        if (latency < 50 && throughput > 100 && packetLoss < 1) {
            return QualityOfService.EXCELLENT;
        } else if (latency < 100 && throughput > 50 && packetLoss < 3) {
            return QualityOfService.GOOD;
        } else if (latency < 200 && throughput > 20 && packetLoss < 5) {
            return QualityOfService.FAIR;
        } else {
            return QualityOfService.POOR;
        }
    }
    
    public enum QualityOfService {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR,
        UNKNOWN
    }
}