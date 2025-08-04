package com.telecom.networkoptimizer.repository;

import com.telecom.networkoptimizer.model.NetworkNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NetworkNodeRepository extends MongoRepository<NetworkNode, String> {
    
    Optional<NetworkNode> findByName(String name);
    
    List<NetworkNode> findByType(NetworkNode.NodeType type);
    
    List<NetworkNode> findByStatus(NetworkNode.NodeStatus status);
    
    List<NetworkNode> findByLocationCity(String city);
    
    List<NetworkNode> findByLocationRegion(String region);
    
    @Query("{ 'currentLoad' : { $gt: ?0 } }")
    List<NetworkNode> findByCurrentLoadGreaterThan(Double load);
    
    @Query("{ 'status': ?0, 'type': ?1 }")
    List<NetworkNode> findByStatusAndType(NetworkNode.NodeStatus status, NetworkNode.NodeType type);
    
    @Query("{ 'location.latitude': { $gte: ?0, $lte: ?1 }, 'location.longitude': { $gte: ?2, $lte: ?3 } }")
    List<NetworkNode> findNodesInRegion(Double minLat, Double maxLat, Double minLng, Double maxLng);
    
    @Query("{ $expr: { $gt: [ { $divide: ['$currentLoad', '$capacity'] }, ?0 ] } }")
    List<NetworkNode> findOverloadedNodes(Double threshold);
    
    @Query("{ 'connectedNodes': { $in: [?0] } }")
    List<NetworkNode> findConnectedToNode(String nodeId);
    
    long countByStatus(NetworkNode.NodeStatus status);
    
    long countByType(NetworkNode.NodeType type);
}