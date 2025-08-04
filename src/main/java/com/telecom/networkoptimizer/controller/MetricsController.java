package com.telecom.networkoptimizer.controller;

import com.telecom.networkoptimizer.model.NetworkMetrics;
import com.telecom.networkoptimizer.service.NetworkMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Network Metrics", description = "APIs for recording and analyzing network performance metrics")
public class MetricsController {
    
    private final NetworkMetricsService networkMetricsService;
    
    @Operation(
        summary = "Record network metrics",
        description = "Records performance metrics for a network node"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Metrics recorded successfully",
                    content = @Content(schema = @Schema(implementation = NetworkMetrics.class))),
        @ApiResponse(responseCode = "400", description = "Invalid metrics data")
    })
    @PostMapping
    public ResponseEntity<NetworkMetrics> recordMetrics(
            @Valid @RequestBody NetworkMetrics metrics) {
        
        log.debug("Recording metrics for node: {}", metrics.getNodeId());
        
        NetworkMetrics recordedMetrics = networkMetricsService.recordMetrics(metrics);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordedMetrics);
    }
    
    @Operation(
        summary = "Get metrics by node",
        description = "Retrieves all metrics for a specific network node"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved node metrics")
    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<NetworkMetrics>> getMetricsByNode(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getMetricsByNode(nodeId);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get metrics by node and time range",
        description = "Retrieves metrics for a specific node within a time range"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered metrics")
    @GetMapping("/node/{nodeId}/range")
    public ResponseEntity<List<NetworkMetrics>> getMetricsByNodeAndTimeRange(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId,
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getMetricsByNodeAndTimeRange(nodeId, start, end);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get recent metrics",
        description = "Retrieves recent metrics for a node within the specified number of hours"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent metrics")
    @GetMapping("/node/{nodeId}/recent")
    public ResponseEntity<List<NetworkMetrics>> getRecentMetrics(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId,
            @Parameter(description = "Number of hours to look back", required = true) @RequestParam(defaultValue = "24") int hours) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getRecentMetrics(nodeId, hours);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get latest metrics for all nodes",
        description = "Retrieves the most recent metrics for all network nodes"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved latest metrics")
    @GetMapping("/latest")
    public ResponseEntity<List<NetworkMetrics>> getLatestMetricsForAllNodes() {
        List<NetworkMetrics> metrics = networkMetricsService.getLatestMetricsForAllNodes();
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get high latency nodes",
        description = "Retrieves nodes with latency above the specified threshold"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved high latency nodes")
    @GetMapping("/high-latency")
    public ResponseEntity<List<NetworkMetrics>> getHighLatencyNodes(
            @Parameter(description = "Latency threshold in milliseconds") 
            @RequestParam(required = false, defaultValue = "100.0") Double threshold) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getHighLatencyNodes(threshold);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get high packet loss nodes",
        description = "Retrieves nodes with packet loss above the specified threshold"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved high packet loss nodes")
    @GetMapping("/high-packet-loss")
    public ResponseEntity<List<NetworkMetrics>> getHighPacketLossNodes(
            @Parameter(description = "Packet loss threshold percentage") 
            @RequestParam(required = false, defaultValue = "5.0") Double threshold) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getHighPacketLossNodes(threshold);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get high CPU utilization nodes",
        description = "Retrieves nodes with CPU utilization above the specified threshold"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved high CPU utilization nodes")
    @GetMapping("/high-cpu")
    public ResponseEntity<List<NetworkMetrics>> getHighCpuUtilizationNodes(
            @Parameter(description = "CPU utilization threshold percentage") 
            @RequestParam(required = false, defaultValue = "80.0") Double threshold) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getHighCpuUtilizationNodes(threshold);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get high memory utilization nodes",
        description = "Retrieves nodes with memory utilization above the specified threshold"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved high memory utilization nodes")
    @GetMapping("/high-memory")
    public ResponseEntity<List<NetworkMetrics>> getHighMemoryUtilizationNodes(
            @Parameter(description = "Memory utilization threshold percentage") 
            @RequestParam(required = false, defaultValue = "85.0") Double threshold) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getHighMemoryUtilizationNodes(threshold);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Get network health summary",
        description = "Retrieves a comprehensive health summary of the entire network"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved network health summary")
    @GetMapping("/health-summary")
    public ResponseEntity<Map<String, Object>> getNetworkHealthSummary() {
        Map<String, Object> healthSummary = networkMetricsService.getNetworkHealthSummary();
        return ResponseEntity.ok(healthSummary);
    }
    
    @Operation(
        summary = "Get performance trends",
        description = "Retrieves performance trends for a specific node over the specified number of days"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved performance trends")
    @GetMapping("/node/{nodeId}/trends")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId,
            @Parameter(description = "Number of days for trend analysis") 
            @RequestParam(defaultValue = "7") int days) {
        
        Map<String, Object> trends = networkMetricsService.getPerformanceTrends(nodeId, days);
        return ResponseEntity.ok(trends);
    }
    
    @Operation(
        summary = "Get top performing nodes",
        description = "Retrieves the top performing nodes based on performance scores"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved top performing nodes")
    @GetMapping("/top-performers")
    public ResponseEntity<List<Map<String, Object>>> getTopPerformingNodes(
            @Parameter(description = "Maximum number of nodes to return") 
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> topNodes = networkMetricsService.getTopPerformingNodes(limit);
        return ResponseEntity.ok(topNodes);
    }
    
    @Operation(
        summary = "Get worst performing nodes",
        description = "Retrieves the worst performing nodes that need attention"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved worst performing nodes")
    @GetMapping("/worst-performers")
    public ResponseEntity<List<Map<String, Object>>> getWorstPerformingNodes(
            @Parameter(description = "Maximum number of nodes to return") 
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> worstNodes = networkMetricsService.getWorstPerformingNodes(limit);
        return ResponseEntity.ok(worstNodes);
    }
    
    @Operation(
        summary = "Get average metrics for node",
        description = "Retrieves average metrics for a node over a specified time period"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved average metrics")
    @GetMapping("/node/{nodeId}/average")
    public ResponseEntity<Map<String, Object>> getAverageMetricsForNode(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId,
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        Map<String, Object> averageMetrics = networkMetricsService.getAverageMetricsForNode(nodeId, start, end);
        return ResponseEntity.ok(averageMetrics);
    }
    
    @Operation(
        summary = "Get metrics by time range",
        description = "Retrieves all network metrics within a specified time range"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved metrics by time range")
    @GetMapping("/range")
    public ResponseEntity<List<NetworkMetrics>> getMetricsByTimeRange(
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<NetworkMetrics> metrics = networkMetricsService.getMetricsByTimeRange(start, end);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(
        summary = "Delete metrics for node",
        description = "Deletes all metrics data for a specific node"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Metrics deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Node not found")
    })
    @DeleteMapping("/node/{nodeId}")
    public ResponseEntity<Void> deleteMetricsForNode(
            @Parameter(description = "Node ID", required = true) @PathVariable String nodeId) {
        
        log.info("Deleting metrics for node: {}", nodeId);
        
        try {
            networkMetricsService.deleteMetricsForNode(nodeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Batch record metrics",
        description = "Records multiple metrics entries in a single request"
    )
    @ApiResponse(responseCode = "201", description = "Batch metrics recorded successfully")
    @PostMapping("/batch")
    public ResponseEntity<List<NetworkMetrics>> recordBatchMetrics(
            @Valid @RequestBody List<NetworkMetrics> metricsList) {
        
        log.info("Recording batch metrics: {} entries", metricsList.size());
        
        List<NetworkMetrics> recordedMetrics = metricsList.stream()
                .map(networkMetricsService::recordMetrics)
                .toList();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(recordedMetrics);
    }
    
    @Operation(
        summary = "Get quality of service distribution",
        description = "Retrieves the distribution of quality of service levels across the network"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved QoS distribution")
    @GetMapping("/qos-distribution")
    public ResponseEntity<Map<String, Object>> getQosDistribution() {
        List<NetworkMetrics> latestMetrics = networkMetricsService.getLatestMetricsForAllNodes();
        
        Map<NetworkMetrics.QualityOfService, Long> qosDistribution = latestMetrics.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NetworkMetrics::getQualityOfService,
                    java.util.stream.Collectors.counting()
                ));
        
        Map<String, Object> result = Map.of(
            "totalNodes", latestMetrics.size(),
            "qosDistribution", qosDistribution,
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(result);
    }
}