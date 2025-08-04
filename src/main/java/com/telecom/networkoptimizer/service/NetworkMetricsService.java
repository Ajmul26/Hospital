package com.telecom.networkoptimizer.service;

import com.telecom.networkoptimizer.model.NetworkMetrics;
import com.telecom.networkoptimizer.repository.NetworkMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkMetricsService {
    
    private final NetworkMetricsRepository networkMetricsRepository;
    
    public NetworkMetrics recordMetrics(NetworkMetrics metrics) {
        log.debug("Recording metrics for node: {}", metrics.getNodeId());
        
        if (metrics.getTimestamp() == null) {
            metrics.setTimestamp(LocalDateTime.now());
        }
        
        return networkMetricsRepository.save(metrics);
    }
    
    public List<NetworkMetrics> getMetricsByNode(String nodeId) {
        return networkMetricsRepository.findByNodeId(nodeId);
    }
    
    public List<NetworkMetrics> getMetricsByNodeAndTimeRange(String nodeId, LocalDateTime start, LocalDateTime end) {
        return networkMetricsRepository.findByNodeIdAndTimestampBetween(nodeId, start, end);
    }
    
    public List<NetworkMetrics> getMetricsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return networkMetricsRepository.findByTimestampBetween(start, end);
    }
    
    public List<NetworkMetrics> getRecentMetrics(String nodeId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return networkMetricsRepository.findRecentMetrics(nodeId, since);
    }
    
    public List<NetworkMetrics> getLatestMetricsForAllNodes() {
        return networkMetricsRepository.findLatestMetricsForAllNodes();
    }
    
    public List<NetworkMetrics> getHighLatencyNodes(Double latencyThreshold) {
        if (latencyThreshold == null) {
            latencyThreshold = 100.0; // Default 100ms threshold
        }
        return networkMetricsRepository.findByLatencyGreaterThan(latencyThreshold);
    }
    
    public List<NetworkMetrics> getHighPacketLossNodes(Double packetLossThreshold) {
        if (packetLossThreshold == null) {
            packetLossThreshold = 5.0; // Default 5% threshold
        }
        return networkMetricsRepository.findByPacketLossGreaterThan(packetLossThreshold);
    }
    
    public List<NetworkMetrics> getHighCpuUtilizationNodes(Double cpuThreshold) {
        if (cpuThreshold == null) {
            cpuThreshold = 80.0; // Default 80% threshold
        }
        return networkMetricsRepository.findByHighCpuUtilization(cpuThreshold);
    }
    
    public List<NetworkMetrics> getHighMemoryUtilizationNodes(Double memoryThreshold) {
        if (memoryThreshold == null) {
            memoryThreshold = 85.0; // Default 85% threshold
        }
        return networkMetricsRepository.findByHighMemoryUtilization(memoryThreshold);
    }
    
    public Map<String, Object> getAverageMetricsForNode(String nodeId, LocalDateTime start, LocalDateTime end) {
        List<Object> results = networkMetricsRepository.getAverageMetrics(nodeId, start, end);
        
        if (results.isEmpty()) {
            return new HashMap<>();
        }
        
        // Convert aggregation result to map
        // This would need proper implementation based on actual aggregation result structure
        Map<String, Object> averageMetrics = new HashMap<>();
        averageMetrics.put("nodeId", nodeId);
        averageMetrics.put("period", start + " to " + end);
        
        return averageMetrics;
    }
    
    public Map<String, Object> getNetworkHealthSummary() {
        List<NetworkMetrics> latestMetrics = getLatestMetricsForAllNodes();
        
        Map<String, Object> summary = new HashMap<>();
        
        if (latestMetrics.isEmpty()) {
            summary.put("status", "NO_DATA");
            return summary;
        }
        
        // Calculate overall health metrics
        OptionalDouble avgLatency = latestMetrics.stream()
            .filter(m -> m.getLatency() != null)
            .mapToDouble(NetworkMetrics::getLatency)
            .average();
        
        OptionalDouble avgThroughput = latestMetrics.stream()
            .filter(m -> m.getThroughput() != null)
            .mapToDouble(NetworkMetrics::getThroughput)
            .average();
        
        OptionalDouble avgPacketLoss = latestMetrics.stream()
            .filter(m -> m.getPacketLoss() != null)
            .mapToDouble(NetworkMetrics::getPacketLoss)
            .average();
        
        OptionalDouble avgCpuUtilization = latestMetrics.stream()
            .filter(m -> m.getCpuUtilization() != null)
            .mapToDouble(NetworkMetrics::getCpuUtilization)
            .average();
        
        // Count nodes by quality of service
        Map<NetworkMetrics.QualityOfService, Long> qosDistribution = latestMetrics.stream()
            .collect(Collectors.groupingBy(
                NetworkMetrics::getQualityOfService,
                Collectors.counting()
            ));
        
        // Count problematic nodes
        long highLatencyNodes = latestMetrics.stream()
            .filter(m -> m.getLatency() != null && m.getLatency() > 100)
            .count();
        
        long highPacketLossNodes = latestMetrics.stream()
            .filter(m -> m.getPacketLoss() != null && m.getPacketLoss() > 5)
            .count();
        
        long overloadedNodes = latestMetrics.stream()
            .filter(m -> m.getCpuUtilization() != null && m.getCpuUtilization() > 80)
            .count();
        
        // Determine overall health status
        String healthStatus = determineOverallHealth(qosDistribution, latestMetrics.size());
        
        // Build summary
        summary.put("totalNodes", latestMetrics.size());
        summary.put("healthStatus", healthStatus);
        summary.put("averageLatency", avgLatency.orElse(0.0));
        summary.put("averageThroughput", avgThroughput.orElse(0.0));
        summary.put("averagePacketLoss", avgPacketLoss.orElse(0.0));
        summary.put("averageCpuUtilization", avgCpuUtilization.orElse(0.0));
        summary.put("qosDistribution", qosDistribution);
        summary.put("issuesCount", Map.of(
            "highLatency", highLatencyNodes,
            "highPacketLoss", highPacketLossNodes,
            "overloaded", overloadedNodes
        ));
        summary.put("lastUpdated", LocalDateTime.now());
        
        return summary;
    }
    
    public Map<String, Object> getPerformanceTrends(String nodeId, int days) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);
        
        List<NetworkMetrics> metrics = getMetricsByNodeAndTimeRange(nodeId, start, end);
        
        if (metrics.isEmpty()) {
            return Map.of("nodeId", nodeId, "status", "NO_DATA");
        }
        
        // Group metrics by day for trend analysis
        Map<String, List<NetworkMetrics>> dailyMetrics = metrics.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTimestamp().toLocalDate().toString()
            ));
        
        Map<String, Map<String, Double>> trends = new TreeMap<>();
        
        for (Map.Entry<String, List<NetworkMetrics>> entry : dailyMetrics.entrySet()) {
            String date = entry.getKey();
            List<NetworkMetrics> dayMetrics = entry.getValue();
            
            Map<String, Double> dayAverages = new HashMap<>();
            dayAverages.put("latency", dayMetrics.stream()
                .filter(m -> m.getLatency() != null)
                .mapToDouble(NetworkMetrics::getLatency)
                .average().orElse(0.0));
            
            dayAverages.put("throughput", dayMetrics.stream()
                .filter(m -> m.getThroughput() != null)
                .mapToDouble(NetworkMetrics::getThroughput)
                .average().orElse(0.0));
            
            dayAverages.put("packetLoss", dayMetrics.stream()
                .filter(m -> m.getPacketLoss() != null)
                .mapToDouble(NetworkMetrics::getPacketLoss)
                .average().orElse(0.0));
            
            dayAverages.put("cpuUtilization", dayMetrics.stream()
                .filter(m -> m.getCpuUtilization() != null)
                .mapToDouble(NetworkMetrics::getCpuUtilization)
                .average().orElse(0.0));
            
            trends.put(date, dayAverages);
        }
        
        return Map.of(
            "nodeId", nodeId,
            "period", days + " days",
            "trends", trends,
            "totalDataPoints", metrics.size()
        );
    }
    
    public List<Map<String, Object>> getTopPerformingNodes(int limit) {
        List<NetworkMetrics> latestMetrics = getLatestMetricsForAllNodes();
        
        return latestMetrics.stream()
            .filter(m -> m.getLatency() != null && m.getThroughput() != null && m.getPacketLoss() != null)
            .sorted((m1, m2) -> {
                // Sort by quality of service and performance metrics
                double score1 = calculatePerformanceScore(m1);
                double score2 = calculatePerformanceScore(m2);
                return Double.compare(score2, score1); // Higher score is better
            })
            .limit(limit)
            .map(this::metricsToSummaryMap)
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getWorstPerformingNodes(int limit) {
        List<NetworkMetrics> latestMetrics = getLatestMetricsForAllNodes();
        
        return latestMetrics.stream()
            .filter(m -> m.getLatency() != null && m.getThroughput() != null && m.getPacketLoss() != null)
            .sorted((m1, m2) -> {
                // Sort by quality of service and performance metrics (ascending for worst)
                double score1 = calculatePerformanceScore(m1);
                double score2 = calculatePerformanceScore(m2);
                return Double.compare(score1, score2); // Lower score is worse
            })
            .limit(limit)
            .map(this::metricsToSummaryMap)
            .collect(Collectors.toList());
    }
    
    private String determineOverallHealth(Map<NetworkMetrics.QualityOfService, Long> qosDistribution, int totalNodes) {
        long excellentNodes = qosDistribution.getOrDefault(NetworkMetrics.QualityOfService.EXCELLENT, 0L);
        long goodNodes = qosDistribution.getOrDefault(NetworkMetrics.QualityOfService.GOOD, 0L);
        long fairNodes = qosDistribution.getOrDefault(NetworkMetrics.QualityOfService.FAIR, 0L);
        long poorNodes = qosDistribution.getOrDefault(NetworkMetrics.QualityOfService.POOR, 0L);
        
        double excellentPercentage = (excellentNodes / (double) totalNodes) * 100;
        double poorPercentage = (poorNodes / (double) totalNodes) * 100;
        
        if (excellentPercentage >= 80) {
            return "EXCELLENT";
        } else if (excellentPercentage + goodNodes >= 70) {
            return "GOOD";
        } else if (poorPercentage <= 20) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
    
    private double calculatePerformanceScore(NetworkMetrics metrics) {
        // Calculate a composite performance score (0-100)
        double latencyScore = Math.max(0, 100 - (metrics.getLatency() / 2)); // Lower latency is better
        double throughputScore = Math.min(100, metrics.getThroughput()); // Higher throughput is better
        double packetLossScore = Math.max(0, 100 - (metrics.getPacketLoss() * 10)); // Lower packet loss is better
        
        return (latencyScore + throughputScore + packetLossScore) / 3;
    }
    
    private Map<String, Object> metricsToSummaryMap(NetworkMetrics metrics) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("nodeId", metrics.getNodeId());
        summary.put("latency", metrics.getLatency());
        summary.put("throughput", metrics.getThroughput());
        summary.put("packetLoss", metrics.getPacketLoss());
        summary.put("qos", metrics.getQualityOfService());
        summary.put("performanceScore", calculatePerformanceScore(metrics));
        summary.put("timestamp", metrics.getTimestamp());
        return summary;
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupOldMetrics() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30); // Keep 30 days of data
        log.info("Cleaning up metrics older than {}", cutoff);
        
        try {
            networkMetricsRepository.deleteByTimestampBefore(cutoff);
            log.info("Successfully cleaned up old metrics");
        } catch (Exception e) {
            log.error("Failed to cleanup old metrics", e);
        }
    }
    
    public void deleteMetricsForNode(String nodeId) {
        List<NetworkMetrics> nodeMetrics = networkMetricsRepository.findByNodeId(nodeId);
        networkMetricsRepository.deleteAll(nodeMetrics);
        log.info("Deleted {} metrics for node {}", nodeMetrics.size(), nodeId);
    }
}