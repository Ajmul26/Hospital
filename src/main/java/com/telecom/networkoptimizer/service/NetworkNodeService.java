package com.telecom.networkoptimizer.service;

import com.telecom.networkoptimizer.model.NetworkNode;
import com.telecom.networkoptimizer.repository.NetworkNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkNodeService {
    
    private final NetworkNodeRepository networkNodeRepository;
    
    public NetworkNode createNode(NetworkNode node) {
        log.info("Creating new network node: {}", node.getName());
        
        // Validate unique name
        if (networkNodeRepository.findByName(node.getName()).isPresent()) {
            throw new IllegalArgumentException("Node with name " + node.getName() + " already exists");
        }
        
        // Set default values
        if (node.getStatus() == null) {
            node.setStatus(NetworkNode.NodeStatus.INACTIVE);
        }
        
        if (node.getCurrentLoad() == null) {
            node.setCurrentLoad(0.0);
        }
        
        if (node.getUsedBandwidth() == null) {
            node.setUsedBandwidth(0.0);
        }
        
        return networkNodeRepository.save(node);
    }
    
    public Optional<NetworkNode> getNodeById(String id) {
        return networkNodeRepository.findById(id);
    }
    
    public Optional<NetworkNode> getNodeByName(String name) {
        return networkNodeRepository.findByName(name);
    }
    
    public List<NetworkNode> getAllNodes() {
        return networkNodeRepository.findAll();
    }
    
    public Page<NetworkNode> getAllNodes(Pageable pageable) {
        return networkNodeRepository.findAll(pageable);
    }
    
    public List<NetworkNode> getNodesByType(NetworkNode.NodeType type) {
        return networkNodeRepository.findByType(type);
    }
    
    public List<NetworkNode> getNodesByStatus(NetworkNode.NodeStatus status) {
        return networkNodeRepository.findByStatus(status);
    }
    
    public List<NetworkNode> getNodesByLocation(String city, String region) {
        if (city != null && !city.isEmpty()) {
            return networkNodeRepository.findByLocationCity(city);
        } else if (region != null && !region.isEmpty()) {
            return networkNodeRepository.findByLocationRegion(region);
        }
        return List.of();
    }
    
    public List<NetworkNode> getOverloadedNodes(Double threshold) {
        if (threshold == null) {
            threshold = 0.8; // Default 80% threshold
        }
        return networkNodeRepository.findOverloadedNodes(threshold);
    }
    
    public List<NetworkNode> getNodesInRegion(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return networkNodeRepository.findNodesInRegion(minLat, maxLat, minLng, maxLng);
    }
    
    public NetworkNode updateNode(String id, NetworkNode updatedNode) {
        log.info("Updating network node: {}", id);
        
        return networkNodeRepository.findById(id)
            .map(existingNode -> {
                // Update fields
                if (updatedNode.getName() != null) {
                    existingNode.setName(updatedNode.getName());
                }
                if (updatedNode.getStatus() != null) {
                    existingNode.setStatus(updatedNode.getStatus());
                }
                if (updatedNode.getCurrentLoad() != null) {
                    existingNode.setCurrentLoad(updatedNode.getCurrentLoad());
                }
                if (updatedNode.getAvailableBandwidth() != null) {
                    existingNode.setAvailableBandwidth(updatedNode.getAvailableBandwidth());
                }
                if (updatedNode.getUsedBandwidth() != null) {
                    existingNode.setUsedBandwidth(updatedNode.getUsedBandwidth());
                }
                if (updatedNode.getConnectedNodes() != null) {
                    existingNode.setConnectedNodes(updatedNode.getConnectedNodes());
                }
                if (updatedNode.getPerformanceMetrics() != null) {
                    existingNode.setPerformanceMetrics(updatedNode.getPerformanceMetrics());
                }
                if (updatedNode.getConfiguration() != null) {
                    existingNode.setConfiguration(updatedNode.getConfiguration());
                }
                
                // Check if node is overloaded and update status
                if (existingNode.isOverloaded() && existingNode.getStatus() == NetworkNode.NodeStatus.ACTIVE) {
                    existingNode.setStatus(NetworkNode.NodeStatus.OVERLOADED);
                    log.warn("Node {} is overloaded with {}% utilization", 
                        existingNode.getName(), existingNode.getUtilizationPercentage());
                }
                
                return networkNodeRepository.save(existingNode);
            })
            .orElseThrow(() -> new IllegalArgumentException("Node with id " + id + " not found"));
    }
    
    public void deleteNode(String id) {
        log.info("Deleting network node: {}", id);
        
        if (!networkNodeRepository.existsById(id)) {
            throw new IllegalArgumentException("Node with id " + id + " not found");
        }
        
        networkNodeRepository.deleteById(id);
    }
    
    public void updateNodeLoad(String nodeId, Double newLoad) {
        log.debug("Updating load for node {}: {}", nodeId, newLoad);
        
        networkNodeRepository.findById(nodeId)
            .ifPresent(node -> {
                node.setCurrentLoad(newLoad);
                
                // Update status based on load
                if (node.isOverloaded()) {
                    node.setStatus(NetworkNode.NodeStatus.OVERLOADED);
                } else if (node.getStatus() == NetworkNode.NodeStatus.OVERLOADED) {
                    node.setStatus(NetworkNode.NodeStatus.ACTIVE);
                }
                
                networkNodeRepository.save(node);
            });
    }
    
    public Map<String, Object> getNetworkStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalNodes", networkNodeRepository.count());
        stats.put("activeNodes", networkNodeRepository.countByStatus(NetworkNode.NodeStatus.ACTIVE));
        stats.put("inactiveNodes", networkNodeRepository.countByStatus(NetworkNode.NodeStatus.INACTIVE));
        stats.put("overloadedNodes", networkNodeRepository.countByStatus(NetworkNode.NodeStatus.OVERLOADED));
        stats.put("maintenanceNodes", networkNodeRepository.countByStatus(NetworkNode.NodeStatus.MAINTENANCE));
        stats.put("errorNodes", networkNodeRepository.countByStatus(NetworkNode.NodeStatus.ERROR));
        
        // Count by type
        for (NetworkNode.NodeType type : NetworkNode.NodeType.values()) {
            stats.put(type.name().toLowerCase() + "Count", networkNodeRepository.countByType(type));
        }
        
        return stats;
    }
    
    public List<NetworkNode> getConnectedNodes(String nodeId) {
        return networkNodeRepository.findConnectedToNode(nodeId);
    }
    
    public void performHealthCheck() {
        log.info("Performing network health check");
        
        List<NetworkNode> overloadedNodes = getOverloadedNodes(0.8);
        for (NetworkNode node : overloadedNodes) {
            log.warn("Node {} is overloaded: {}% utilization", 
                node.getName(), node.getUtilizationPercentage());
        }
        
        List<NetworkNode> errorNodes = networkNodeRepository.findByStatus(NetworkNode.NodeStatus.ERROR);
        if (!errorNodes.isEmpty()) {
            log.error("Found {} nodes in error state", errorNodes.size());
        }
    }
}