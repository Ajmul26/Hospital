package com.telecom.networkoptimizer.repository;

import com.telecom.networkoptimizer.model.NetworkMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NetworkMetricsRepository extends MongoRepository<NetworkMetrics, String> {
    
    List<NetworkMetrics> findByNodeId(String nodeId);
    
    List<NetworkMetrics> findByNodeIdAndTimestampBetween(String nodeId, LocalDateTime start, LocalDateTime end);
    
    List<NetworkMetrics> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'nodeId': ?0, 'timestamp': { $gte: ?1 } }")
    List<NetworkMetrics> findRecentMetrics(String nodeId, LocalDateTime since);
    
    @Query("{ 'latency': { $gt: ?0 } }")
    List<NetworkMetrics> findByLatencyGreaterThan(Double latency);
    
    @Query("{ 'packetLoss': { $gt: ?0 } }")
    List<NetworkMetrics> findByPacketLossGreaterThan(Double packetLoss);
    
    @Query("{ 'cpuUtilization': { $gt: ?0 } }")
    List<NetworkMetrics> findByHighCpuUtilization(Double threshold);
    
    @Query("{ 'memoryUtilization': { $gt: ?0 } }")
    List<NetworkMetrics> findByHighMemoryUtilization(Double threshold);
    
    // Aggregation to get average metrics for a node over time period
    @Aggregation(pipeline = {
        "{ $match: { 'nodeId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } } }",
        "{ $group: { '_id': '$nodeId', " +
            "'avgLatency': { $avg: '$latency' }, " +
            "'avgThroughput': { $avg: '$throughput' }, " +
            "'avgPacketLoss': { $avg: '$packetLoss' }, " +
            "'avgCpuUtilization': { $avg: '$cpuUtilization' }, " +
            "'avgMemoryUtilization': { $avg: '$memoryUtilization' } " +
        "} }"
    })
    List<Object> getAverageMetrics(String nodeId, LocalDateTime start, LocalDateTime end);
    
    // Find the latest metrics for each node
    @Aggregation(pipeline = {
        "{ $sort: { 'timestamp': -1 } }",
        "{ $group: { '_id': '$nodeId', 'latestMetric': { $first: '$$ROOT' } } }",
        "{ $replaceRoot: { 'newRoot': '$latestMetric' } }"
    })
    List<NetworkMetrics> findLatestMetricsForAllNodes();
    
    void deleteByTimestampBefore(LocalDateTime cutoff);
}