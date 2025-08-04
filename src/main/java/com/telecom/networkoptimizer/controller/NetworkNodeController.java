package com.telecom.networkoptimizer.controller;

import com.telecom.networkoptimizer.model.NetworkNode;
import com.telecom.networkoptimizer.service.NetworkNodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Network Nodes", description = "APIs for managing telecommunications network nodes")
public class NetworkNodeController {
    
    private final NetworkNodeService networkNodeService;
    
    @Operation(
        summary = "Create a new network node",
        description = "Creates a new network node in the telecommunications infrastructure"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Node created successfully",
                    content = @Content(schema = @Schema(implementation = NetworkNode.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Node with name already exists")
    })
    @PostMapping
    public ResponseEntity<NetworkNode> createNode(
            @Valid @RequestBody NetworkNode node) {
        
        log.info("Creating new network node: {}", node.getName());
        
        try {
            NetworkNode createdNode = networkNodeService.createNode(node);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @Operation(
        summary = "Get all network nodes",
        description = "Retrieves a paginated list of all network nodes"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nodes")
    @GetMapping
    public ResponseEntity<Page<NetworkNode>> getAllNodes(
            @Parameter(description = "Pagination information") Pageable pageable) {
        
        Page<NetworkNode> nodes = networkNodeService.getAllNodes(pageable);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Get network node by ID",
        description = "Retrieves a specific network node by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Node found",
                    content = @Content(schema = @Schema(implementation = NetworkNode.class))),
        @ApiResponse(responseCode = "404", description = "Node not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NetworkNode> getNodeById(
            @Parameter(description = "Node ID", required = true) @PathVariable String id) {
        
        return networkNodeService.getNodeById(id)
                .map(node -> ResponseEntity.ok(node))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Get network node by name",
        description = "Retrieves a specific network node by its name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Node found"),
        @ApiResponse(responseCode = "404", description = "Node not found")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<NetworkNode> getNodeByName(
            @Parameter(description = "Node name", required = true) @PathVariable String name) {
        
        return networkNodeService.getNodeByName(name)
                .map(node -> ResponseEntity.ok(node))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Get nodes by type",
        description = "Retrieves all network nodes of a specific type"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nodes by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NetworkNode>> getNodesByType(
            @Parameter(description = "Node type", required = true) @PathVariable NetworkNode.NodeType type) {
        
        List<NetworkNode> nodes = networkNodeService.getNodesByType(type);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Get nodes by status",
        description = "Retrieves all network nodes with a specific status"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nodes by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NetworkNode>> getNodesByStatus(
            @Parameter(description = "Node status", required = true) @PathVariable NetworkNode.NodeStatus status) {
        
        List<NetworkNode> nodes = networkNodeService.getNodesByStatus(status);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Get nodes by location",
        description = "Retrieves network nodes filtered by city or region"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nodes by location")
    @GetMapping("/location")
    public ResponseEntity<List<NetworkNode>> getNodesByLocation(
            @Parameter(description = "City name") @RequestParam(required = false) String city,
            @Parameter(description = "Region name") @RequestParam(required = false) String region) {
        
        List<NetworkNode> nodes = networkNodeService.getNodesByLocation(city, region);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Get overloaded nodes",
        description = "Retrieves network nodes that exceed the specified utilization threshold"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved overloaded nodes")
    @GetMapping("/overloaded")
    public ResponseEntity<List<NetworkNode>> getOverloadedNodes(
            @Parameter(description = "Utilization threshold (0.0-1.0), defaults to 0.8") 
            @RequestParam(required = false, defaultValue = "0.8") Double threshold) {
        
        List<NetworkNode> nodes = networkNodeService.getOverloadedNodes(threshold);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Get nodes in geographic region",
        description = "Retrieves network nodes within specified geographic coordinates"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved nodes in region")
    @GetMapping("/region")
    public ResponseEntity<List<NetworkNode>> getNodesInRegion(
            @Parameter(description = "Minimum latitude", required = true) @RequestParam Double minLat,
            @Parameter(description = "Maximum latitude", required = true) @RequestParam Double maxLat,
            @Parameter(description = "Minimum longitude", required = true) @RequestParam Double minLng,
            @Parameter(description = "Maximum longitude", required = true) @RequestParam Double maxLng) {
        
        List<NetworkNode> nodes = networkNodeService.getNodesInRegion(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(nodes);
    }
    
    @Operation(
        summary = "Update network node",
        description = "Updates an existing network node with new information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Node updated successfully"),
        @ApiResponse(responseCode = "404", description = "Node not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NetworkNode> updateNode(
            @Parameter(description = "Node ID", required = true) @PathVariable String id,
            @Valid @RequestBody NetworkNode updatedNode) {
        
        log.info("Updating network node: {}", id);
        
        try {
            NetworkNode updated = networkNodeService.updateNode(id, updatedNode);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Update node load",
        description = "Updates the current load of a specific network node"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Node load updated successfully"),
        @ApiResponse(responseCode = "404", description = "Node not found")
    })
    @PatchMapping("/{id}/load")
    public ResponseEntity<Void> updateNodeLoad(
            @Parameter(description = "Node ID", required = true) @PathVariable String id,
            @Parameter(description = "New load value", required = true) @RequestParam Double load) {
        
        log.debug("Updating load for node {}: {}", id, load);
        
        try {
            networkNodeService.updateNodeLoad(id, load);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Delete network node",
        description = "Removes a network node from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Node deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Node not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(
            @Parameter(description = "Node ID", required = true) @PathVariable String id) {
        
        log.info("Deleting network node: {}", id);
        
        try {
            networkNodeService.deleteNode(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Get network statistics",
        description = "Retrieves comprehensive statistics about the network infrastructure"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved network statistics")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getNetworkStatistics() {
        Map<String, Object> stats = networkNodeService.getNetworkStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @Operation(
        summary = "Get connected nodes",
        description = "Retrieves all nodes connected to a specific node"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved connected nodes")
    @GetMapping("/{id}/connected")
    public ResponseEntity<List<NetworkNode>> getConnectedNodes(
            @Parameter(description = "Node ID", required = true) @PathVariable String id) {
        
        List<NetworkNode> connectedNodes = networkNodeService.getConnectedNodes(id);
        return ResponseEntity.ok(connectedNodes);
    }
    
    @Operation(
        summary = "Perform network health check",
        description = "Performs a comprehensive health check of the network infrastructure"
    )
    @ApiResponse(responseCode = "200", description = "Health check completed successfully")
    @PostMapping("/health-check")
    public ResponseEntity<Map<String, String>> performHealthCheck() {
        log.info("Performing network health check");
        
        networkNodeService.performHealthCheck();
        
        Map<String, String> response = Map.of(
            "status", "completed",
            "message", "Network health check completed successfully"
        );
        
        return ResponseEntity.ok(response);
    }
}