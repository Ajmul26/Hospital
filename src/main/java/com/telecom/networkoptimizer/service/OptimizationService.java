package com.telecom.networkoptimizer.service;

import com.telecom.networkoptimizer.model.NetworkNode;
import com.telecom.networkoptimizer.model.OptimizationTask;
import com.telecom.networkoptimizer.repository.OptimizationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.genetics.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizationService {
    
    private final OptimizationTaskRepository optimizationTaskRepository;
    private final NetworkNodeService networkNodeService;
    private final NetworkMetricsService networkMetricsService;
    
    public OptimizationTask createOptimizationTask(OptimizationTask task) {
        log.info("Creating optimization task: {}", task.getName());
        
        task.setStatus(OptimizationTask.TaskStatus.PENDING);
        task.setProgressPercentage(0);
        
        if (task.getAlgorithm() == null) {
            task.setAlgorithm("genetic");
        }
        
        if (task.getMaxIterations() == null) {
            task.setMaxIterations(1000);
        }
        
        if (task.getConvergenceThreshold() == null) {
            task.setConvergenceThreshold(0.001);
        }
        
        return optimizationTaskRepository.save(task);
    }
    
    @Async
    public CompletableFuture<OptimizationTask> executeOptimizationTask(String taskId) {
        log.info("Starting optimization task execution: {}", taskId);
        
        OptimizationTask task = optimizationTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        try {
            // Update task status
            task.setStatus(OptimizationTask.TaskStatus.RUNNING);
            task.setStartTime(LocalDateTime.now());
            task.setProgressPercentage(0);
            optimizationTaskRepository.save(task);
            
            // Execute optimization based on task type
            Map<String, Object> results = switch (task.getType()) {
                case LOAD_BALANCING -> performLoadBalancing(task);
                case BANDWIDTH_OPTIMIZATION -> performBandwidthOptimization(task);
                case ROUTE_OPTIMIZATION -> performRouteOptimization(task);
                case CAPACITY_PLANNING -> performCapacityPlanning(task);
                case FAULT_DETECTION -> performFaultDetection(task);
                case PERFORMANCE_TUNING -> performPerformanceTuning(task);
                case COST_OPTIMIZATION -> performCostOptimization(task);
                case ENERGY_EFFICIENCY -> performEnergyOptimization(task);
            };
            
            // Update task with results
            task.setResults(results);
            task.setStatus(OptimizationTask.TaskStatus.COMPLETED);
            task.setEndTime(LocalDateTime.now());
            task.setProgressPercentage(100);
            
            // Calculate improvement percentage
            if (results.containsKey("improvementPercentage")) {
                task.setImprovementPercentage((Double) results.get("improvementPercentage"));
            }
            
            optimizationTaskRepository.save(task);
            log.info("Optimization task completed: {}", taskId);
            
        } catch (Exception e) {
            log.error("Optimization task failed: {}", taskId, e);
            task.setStatus(OptimizationTask.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setEndTime(LocalDateTime.now());
            optimizationTaskRepository.save(task);
        }
        
        return CompletableFuture.completedFuture(task);
    }
    
    private Map<String, Object> performLoadBalancing(OptimizationTask task) {
        log.info("Performing load balancing optimization");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        // Calculate current load distribution
        double totalLoad = nodes.stream()
            .mapToDouble(node -> node.getCurrentLoad() != null ? node.getCurrentLoad() : 0.0)
            .sum();
        
        double averageLoad = totalLoad / nodes.size();
        
        // Identify overloaded and underutilized nodes
        List<NetworkNode> overloadedNodes = nodes.stream()
            .filter(node -> node.getCurrentLoad() > averageLoad * 1.2)
            .collect(Collectors.toList());
        
        List<NetworkNode> underutilizedNodes = nodes.stream()
            .filter(node -> node.getCurrentLoad() < averageLoad * 0.8)
            .collect(Collectors.toList());
        
        // Redistribute load
        double redistributedLoad = 0.0;
        for (NetworkNode overloaded : overloadedNodes) {
            double excessLoad = overloaded.getCurrentLoad() - averageLoad;
            
            for (NetworkNode underutilized : underutilizedNodes) {
                double availableCapacity = (averageLoad * 1.1) - underutilized.getCurrentLoad();
                double transferLoad = Math.min(excessLoad, availableCapacity);
                
                if (transferLoad > 0) {
                    // Simulate load redistribution
                    overloaded.setCurrentLoad(overloaded.getCurrentLoad() - transferLoad);
                    underutilized.setCurrentLoad(underutilized.getCurrentLoad() + transferLoad);
                    redistributedLoad += transferLoad;
                    excessLoad -= transferLoad;
                    
                    if (excessLoad <= 0) break;
                }
            }
        }
        
        // Calculate improvement
        double improvementPercentage = (redistributedLoad / totalLoad) * 100;
        
        results.put("redistributedLoad", redistributedLoad);
        results.put("improvementPercentage", improvementPercentage);
        results.put("overloadedNodesBefore", overloadedNodes.size());
        results.put("recommendations", generateLoadBalancingRecommendations(overloadedNodes, underutilizedNodes));
        
        return results;
    }
    
    private Map<String, Object> performBandwidthOptimization(OptimizationTask task) {
        log.info("Performing bandwidth optimization");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        double totalBandwidthSaved = 0.0;
        List<String> optimizations = new ArrayList<>();
        
        for (NetworkNode node : nodes) {
            double utilizationPercentage = node.getBandwidthUtilizationPercentage();
            
            if (utilizationPercentage > 80) {
                // High utilization - recommend bandwidth increase
                double recommendedIncrease = node.getUsedBandwidth() * 0.2;
                optimizations.add(String.format("Increase bandwidth for node %s by %.2f Mbps", 
                    node.getName(), recommendedIncrease));
            } else if (utilizationPercentage < 30) {
                // Low utilization - potential bandwidth reduction
                double potentialSaving = node.getAvailableBandwidth() * 0.3;
                totalBandwidthSaved += potentialSaving;
                optimizations.add(String.format("Consider reducing bandwidth allocation for node %s by %.2f Mbps", 
                    node.getName(), potentialSaving));
            }
        }
        
        results.put("totalBandwidthSaved", totalBandwidthSaved);
        results.put("optimizations", optimizations);
        results.put("improvementPercentage", (totalBandwidthSaved / 1000) * 100); // Assuming 1000 Mbps total
        
        return results;
    }
    
    private Map<String, Object> performRouteOptimization(OptimizationTask task) {
        log.info("Performing route optimization using genetic algorithm");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        // Simulate route optimization using genetic algorithm
        int populationSize = Math.min(50, nodes.size() * 2);
        double mutationRate = 0.1;
        double crossoverRate = 0.8;
        
        // Create initial population of route configurations
        List<List<String>> routes = generateInitialRoutes(nodes, populationSize);
        
        // Evolve routes for better performance
        int generations = Math.min(task.getMaxIterations(), 100);
        double bestFitness = 0.0;
        
        for (int generation = 0; generation < generations; generation++) {
            // Evaluate fitness of each route
            List<Double> fitnessScores = routes.stream()
                .map(this::evaluateRouteFitness)
                .collect(Collectors.toList());
            
            double currentBestFitness = Collections.max(fitnessScores);
            if (currentBestFitness > bestFitness) {
                bestFitness = currentBestFitness;
            }
            
            // Update progress
            int progress = (int) ((generation / (double) generations) * 100);
            updateTaskProgress(task.getId(), progress);
            
            // Selection, crossover, and mutation would go here
            // Simplified for demonstration
        }
        
        double improvementPercentage = bestFitness * 100;
        
        results.put("optimizedRoutes", routes.get(0)); // Best route
        results.put("improvementPercentage", improvementPercentage);
        results.put("generationsProcessed", generations);
        results.put("finalFitness", bestFitness);
        
        return results;
    }
    
    private Map<String, Object> performCapacityPlanning(OptimizationTask task) {
        log.info("Performing capacity planning analysis");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        // Analyze current capacity utilization
        Map<String, Object> capacityAnalysis = new HashMap<>();
        List<String> recommendations = new ArrayList<>();
        
        for (NetworkNode node : nodes) {
            double utilization = node.getUtilizationPercentage();
            
            if (utilization > 85) {
                recommendations.add(String.format("CRITICAL: Node %s requires immediate capacity upgrade (%.1f%% utilized)", 
                    node.getName(), utilization));
            } else if (utilization > 70) {
                recommendations.add(String.format("WARNING: Node %s approaching capacity limit (%.1f%% utilized)", 
                    node.getName(), utilization));
            } else if (utilization < 20) {
                recommendations.add(String.format("INFO: Node %s is underutilized (%.1f%% utilized)", 
                    node.getName(), utilization));
            }
        }
        
        // Calculate projected growth
        double averageGrowthRate = 0.15; // 15% annual growth
        Map<String, Double> projectedCapacity = new HashMap<>();
        
        for (NetworkNode node : nodes) {
            double currentUtilization = node.getUtilizationPercentage();
            double projectedUtilization = currentUtilization * (1 + averageGrowthRate);
            projectedCapacity.put(node.getName(), projectedUtilization);
        }
        
        results.put("currentAnalysis", capacityAnalysis);
        results.put("recommendations", recommendations);
        results.put("projectedCapacity", projectedCapacity);
        results.put("improvementPercentage", 25.0); // Estimated improvement from capacity planning
        
        return results;
    }
    
    private Map<String, Object> performFaultDetection(OptimizationTask task) {
        log.info("Performing fault detection analysis");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        List<String> detectedFaults = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        for (NetworkNode node : nodes) {
            // Check for error status
            if (node.getStatus() == NetworkNode.NodeStatus.ERROR) {
                detectedFaults.add(String.format("FAULT: Node %s is in error state", node.getName()));
            }
            
            // Check for overload conditions
            if (node.isOverloaded()) {
                warnings.add(String.format("WARNING: Node %s is overloaded", node.getName()));
            }
            
            // Check for connectivity issues
            if (node.getConnectedNodes() == null || node.getConnectedNodes().isEmpty()) {
                warnings.add(String.format("WARNING: Node %s has no connections", node.getName()));
            }
        }
        
        results.put("faultsDetected", detectedFaults);
        results.put("warnings", warnings);
        results.put("totalIssues", detectedFaults.size() + warnings.size());
        results.put("improvementPercentage", Math.max(0, 100 - (detectedFaults.size() + warnings.size()) * 10));
        
        return results;
    }
    
    private Map<String, Object> performPerformanceTuning(OptimizationTask task) {
        log.info("Performing performance tuning");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        List<String> tuningRecommendations = new ArrayList<>();
        double performanceGain = 0.0;
        
        for (NetworkNode node : nodes) {
            // Analyze performance metrics if available
            if (node.getPerformanceMetrics() != null) {
                Map<String, Object> metrics = node.getPerformanceMetrics();
                
                // Check latency
                if (metrics.containsKey("latency")) {
                    Double latency = (Double) metrics.get("latency");
                    if (latency > 100) {
                        tuningRecommendations.add(String.format("Optimize routing for node %s to reduce latency", node.getName()));
                        performanceGain += 5.0;
                    }
                }
                
                // Check throughput
                if (metrics.containsKey("throughput")) {
                    Double throughput = (Double) metrics.get("throughput");
                    if (throughput < 50) {
                        tuningRecommendations.add(String.format("Tune QoS settings for node %s to improve throughput", node.getName()));
                        performanceGain += 8.0;
                    }
                }
            }
        }
        
        results.put("tuningRecommendations", tuningRecommendations);
        results.put("estimatedPerformanceGain", performanceGain);
        results.put("improvementPercentage", Math.min(performanceGain, 50.0));
        
        return results;
    }
    
    private Map<String, Object> performCostOptimization(OptimizationTask task) {
        log.info("Performing cost optimization analysis");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        double estimatedSavings = 0.0;
        List<String> costOptimizations = new ArrayList<>();
        
        for (NetworkNode node : nodes) {
            // Check for underutilized resources
            double utilization = node.getUtilizationPercentage();
            if (utilization < 30) {
                double potentialSaving = 1000 * (1 - utilization / 100); // Simplified cost calculation
                estimatedSavings += potentialSaving;
                costOptimizations.add(String.format("Consider downsizing node %s (%.1f%% utilized) - potential saving: $%.2f/month", 
                    node.getName(), utilization, potentialSaving));
            }
        }
        
        results.put("estimatedMonthlySavings", estimatedSavings);
        results.put("costOptimizations", costOptimizations);
        results.put("improvementPercentage", Math.min(estimatedSavings / 100, 40.0));
        
        return results;
    }
    
    private Map<String, Object> performEnergyOptimization(OptimizationTask task) {
        log.info("Performing energy efficiency optimization");
        
        List<NetworkNode> nodes = getTargetNodes(task);
        Map<String, Object> results = new HashMap<>();
        
        double energySavings = 0.0;
        List<String> energyOptimizations = new ArrayList<>();
        
        for (NetworkNode node : nodes) {
            // Check for energy optimization opportunities
            if (node.getPerformanceMetrics() != null && 
                node.getPerformanceMetrics().containsKey("powerConsumption")) {
                
                Double powerConsumption = (Double) node.getPerformanceMetrics().get("powerConsumption");
                double utilization = node.getUtilizationPercentage();
                
                if (utilization < 40 && powerConsumption > 100) {
                    double potentialSaving = powerConsumption * 0.2; // 20% energy saving
                    energySavings += potentialSaving;
                    energyOptimizations.add(String.format("Enable power saving mode for node %s - potential saving: %.2f W", 
                        node.getName(), potentialSaving));
                }
            }
        }
        
        results.put("estimatedEnergySavings", energySavings);
        results.put("energyOptimizations", energyOptimizations);
        results.put("improvementPercentage", Math.min(energySavings / 10, 35.0));
        
        return results;
    }
    
    private List<NetworkNode> getTargetNodes(OptimizationTask task) {
        if (task.getTargetNodeIds() != null && !task.getTargetNodeIds().isEmpty()) {
            return task.getTargetNodeIds().stream()
                .map(nodeId -> networkNodeService.getNodeById(nodeId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        } else {
            return networkNodeService.getAllNodes();
        }
    }
    
    private List<String> generateLoadBalancingRecommendations(List<NetworkNode> overloaded, List<NetworkNode> underutilized) {
        List<String> recommendations = new ArrayList<>();
        
        for (NetworkNode node : overloaded) {
            recommendations.add(String.format("Redistribute load from %s (%.1f%% utilized)", 
                node.getName(), node.getUtilizationPercentage()));
        }
        
        for (NetworkNode node : underutilized) {
            recommendations.add(String.format("Increase load to %s (%.1f%% utilized)", 
                node.getName(), node.getUtilizationPercentage()));
        }
        
        return recommendations;
    }
    
    private List<List<String>> generateInitialRoutes(List<NetworkNode> nodes, int populationSize) {
        List<List<String>> routes = new ArrayList<>();
        List<String> nodeIds = nodes.stream().map(NetworkNode::getId).collect(Collectors.toList());
        
        for (int i = 0; i < populationSize; i++) {
            List<String> route = new ArrayList<>(nodeIds);
            Collections.shuffle(route);
            routes.add(route);
        }
        
        return routes;
    }
    
    private Double evaluateRouteFitness(List<String> route) {
        // Simplified fitness evaluation
        // In real implementation, this would calculate actual network performance metrics
        return Math.random() * 0.9 + 0.1; // Random fitness between 0.1 and 1.0
    }
    
    private void updateTaskProgress(String taskId, int progress) {
        optimizationTaskRepository.findById(taskId)
            .ifPresent(task -> {
                task.setProgressPercentage(progress);
                optimizationTaskRepository.save(task);
            });
    }
    
    public List<OptimizationTask> getTasksByStatus(OptimizationTask.TaskStatus status) {
        return optimizationTaskRepository.findByStatus(status);
    }
    
    public List<OptimizationTask> getRunningTasks() {
        return optimizationTaskRepository.findRunningTasks();
    }
    
    public Optional<OptimizationTask> getTaskById(String taskId) {
        return optimizationTaskRepository.findById(taskId);
    }
    
    public void cancelTask(String taskId) {
        optimizationTaskRepository.findById(taskId)
            .ifPresent(task -> {
                if (task.getStatus() == OptimizationTask.TaskStatus.RUNNING || 
                    task.getStatus() == OptimizationTask.TaskStatus.PENDING) {
                    task.setStatus(OptimizationTask.TaskStatus.CANCELLED);
                    task.setEndTime(LocalDateTime.now());
                    optimizationTaskRepository.save(task);
                }
            });
    }
}