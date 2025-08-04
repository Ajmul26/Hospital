package com.telecom.networkoptimizer.repository;

import com.telecom.networkoptimizer.model.OptimizationTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OptimizationTaskRepository extends MongoRepository<OptimizationTask, String> {
    
    List<OptimizationTask> findByStatus(OptimizationTask.TaskStatus status);
    
    List<OptimizationTask> findByType(OptimizationTask.TaskType type);
    
    List<OptimizationTask> findByPriority(OptimizationTask.Priority priority);
    
    List<OptimizationTask> findByInitiatedBy(String initiatedBy);
    
    @Query("{ 'status': { $in: ?0 } }")
    List<OptimizationTask> findByStatusIn(List<OptimizationTask.TaskStatus> statuses);
    
    @Query("{ 'priority': ?0, 'status': ?1 }")
    List<OptimizationTask> findByPriorityAndStatus(OptimizationTask.Priority priority, OptimizationTask.TaskStatus status);
    
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<OptimizationTask> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'targetNodeIds': { $in: [?0] } }")
    List<OptimizationTask> findByTargetNodeId(String nodeId);
    
    @Query("{ 'status': 'RUNNING' }")
    List<OptimizationTask> findRunningTasks();
    
    @Query("{ 'status': 'PENDING', 'priority': { $in: ['HIGH', 'CRITICAL'] } }")
    List<OptimizationTask> findHighPriorityPendingTasks();
    
    long countByStatus(OptimizationTask.TaskStatus status);
    
    long countByType(OptimizationTask.TaskType type);
    
    @Query("{ 'status': 'COMPLETED', 'endTime': { $gte: ?0 } }")
    List<OptimizationTask> findCompletedTasksSince(LocalDateTime since);
}