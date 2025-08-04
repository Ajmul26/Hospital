package com.telecom.networkoptimizer.controller;

import com.telecom.networkoptimizer.model.OptimizationTask;
import com.telecom.networkoptimizer.service.OptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/optimization")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Network Optimization", description = "APIs for managing network optimization tasks and algorithms")
public class OptimizationController {
    
    private final OptimizationService optimizationService;
    
    @Operation(
        summary = "Create optimization task",
        description = "Creates a new optimization task for network performance improvement"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = OptimizationTask.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/tasks")
    public ResponseEntity<OptimizationTask> createOptimizationTask(
            @Valid @RequestBody OptimizationTask task) {
        
        log.info("Creating optimization task: {}", task.getName());
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Execute optimization task",
        description = "Starts the execution of a pending optimization task asynchronously"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Task execution started"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "409", description = "Task already running or completed")
    })
    @PostMapping("/tasks/{taskId}/execute")
    public ResponseEntity<Map<String, String>> executeOptimizationTask(
            @Parameter(description = "Task ID", required = true) @PathVariable String taskId) {
        
        log.info("Starting execution of optimization task: {}", taskId);
        
        try {
            CompletableFuture<OptimizationTask> future = optimizationService.executeOptimizationTask(taskId);
            
            Map<String, String> response = Map.of(
                "status", "accepted",
                "message", "Task execution started",
                "taskId", taskId
            );
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Get optimization task by ID",
        description = "Retrieves a specific optimization task by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = OptimizationTask.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<OptimizationTask> getOptimizationTask(
            @Parameter(description = "Task ID", required = true) @PathVariable String taskId) {
        
        return optimizationService.getTaskById(taskId)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Get tasks by status",
        description = "Retrieves all optimization tasks with a specific status"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks")
    @GetMapping("/tasks/status/{status}")
    public ResponseEntity<List<OptimizationTask>> getTasksByStatus(
            @Parameter(description = "Task status", required = true) @PathVariable OptimizationTask.TaskStatus status) {
        
        List<OptimizationTask> tasks = optimizationService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(
        summary = "Get running tasks",
        description = "Retrieves all currently running optimization tasks"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved running tasks")
    @GetMapping("/tasks/running")
    public ResponseEntity<List<OptimizationTask>> getRunningTasks() {
        List<OptimizationTask> tasks = optimizationService.getRunningTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(
        summary = "Cancel optimization task",
        description = "Cancels a running or pending optimization task"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "409", description = "Task cannot be cancelled")
    })
    @PostMapping("/tasks/{taskId}/cancel")
    public ResponseEntity<Map<String, String>> cancelOptimizationTask(
            @Parameter(description = "Task ID", required = true) @PathVariable String taskId) {
        
        log.info("Cancelling optimization task: {}", taskId);
        
        try {
            optimizationService.cancelTask(taskId);
            
            Map<String, String> response = Map.of(
                "status", "cancelled",
                "message", "Task cancelled successfully",
                "taskId", taskId
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Load balancing optimization",
        description = "Creates and executes a load balancing optimization task"
    )
    @ApiResponse(responseCode = "201", description = "Load balancing task created and started")
    @PostMapping("/load-balancing")
    public ResponseEntity<OptimizationTask> optimizeLoadBalancing(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "MEDIUM") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Load Balancing Optimization")
                .description("Optimize load distribution across network nodes")
                .type(OptimizationTask.TaskType.LOAD_BALANCING)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        
        // Start execution immediately
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Bandwidth optimization",
        description = "Creates and executes a bandwidth optimization task"
    )
    @ApiResponse(responseCode = "201", description = "Bandwidth optimization task created and started")
    @PostMapping("/bandwidth")
    public ResponseEntity<OptimizationTask> optimizeBandwidth(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "MEDIUM") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Bandwidth Optimization")
                .description("Optimize bandwidth allocation and utilization")
                .type(OptimizationTask.TaskType.BANDWIDTH_OPTIMIZATION)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Route optimization",
        description = "Creates and executes a route optimization task using genetic algorithms"
    )
    @ApiResponse(responseCode = "201", description = "Route optimization task created and started")
    @PostMapping("/routing")
    public ResponseEntity<OptimizationTask> optimizeRouting(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Optimization algorithm") @RequestParam(defaultValue = "genetic") String algorithm,
            @Parameter(description = "Maximum iterations") @RequestParam(defaultValue = "1000") Integer maxIterations,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "HIGH") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Route Optimization")
                .description("Optimize network routing using " + algorithm + " algorithm")
                .type(OptimizationTask.TaskType.ROUTE_OPTIMIZATION)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .algorithm(algorithm)
                .maxIterations(maxIterations)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Capacity planning",
        description = "Creates and executes a capacity planning analysis task"
    )
    @ApiResponse(responseCode = "201", description = "Capacity planning task created and started")
    @PostMapping("/capacity-planning")
    public ResponseEntity<OptimizationTask> performCapacityPlanning(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "HIGH") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Capacity Planning Analysis")
                .description("Analyze current capacity and predict future requirements")
                .type(OptimizationTask.TaskType.CAPACITY_PLANNING)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Fault detection",
        description = "Creates and executes a fault detection analysis task"
    )
    @ApiResponse(responseCode = "201", description = "Fault detection task created and started")
    @PostMapping("/fault-detection")
    public ResponseEntity<OptimizationTask> performFaultDetection(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "CRITICAL") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Fault Detection Analysis")
                .description("Detect and analyze network faults and anomalies")
                .type(OptimizationTask.TaskType.FAULT_DETECTION)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Performance tuning",
        description = "Creates and executes a performance tuning optimization task"
    )
    @ApiResponse(responseCode = "201", description = "Performance tuning task created and started")
    @PostMapping("/performance-tuning")
    public ResponseEntity<OptimizationTask> performPerformanceTuning(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "HIGH") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Performance Tuning")
                .description("Optimize network performance parameters and settings")
                .type(OptimizationTask.TaskType.PERFORMANCE_TUNING)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Cost optimization",
        description = "Creates and executes a cost optimization analysis task"
    )
    @ApiResponse(responseCode = "201", description = "Cost optimization task created and started")
    @PostMapping("/cost-optimization")
    public ResponseEntity<OptimizationTask> performCostOptimization(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "MEDIUM") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Cost Optimization Analysis")
                .description("Analyze and optimize operational costs")
                .type(OptimizationTask.TaskType.COST_OPTIMIZATION)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(
        summary = "Energy efficiency optimization",
        description = "Creates and executes an energy efficiency optimization task"
    )
    @ApiResponse(responseCode = "201", description = "Energy efficiency task created and started")
    @PostMapping("/energy-efficiency")
    public ResponseEntity<OptimizationTask> performEnergyOptimization(
            @Parameter(description = "List of target node IDs") @RequestParam(required = false) List<String> nodeIds,
            @Parameter(description = "Task priority") @RequestParam(defaultValue = "MEDIUM") OptimizationTask.Priority priority) {
        
        OptimizationTask task = OptimizationTask.builder()
                .name("Energy Efficiency Optimization")
                .description("Optimize energy consumption and efficiency")
                .type(OptimizationTask.TaskType.ENERGY_EFFICIENCY)
                .priority(priority)
                .targetNodeIds(nodeIds)
                .build();
        
        OptimizationTask createdTask = optimizationService.createOptimizationTask(task);
        optimizationService.executeOptimizationTask(createdTask.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
}