package com.example.springcrudh2.controller;

import com.example.springcrudh2.service.SplunkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/splunk")
@CrossOrigin(origins = "*")
@Tag(name = "Splunk Management", description = "API for managing Splunk integration and monitoring")
public class SplunkController {
    
    private final SplunkService splunkService;
    
    @Autowired
    public SplunkController(SplunkService splunkService) {
        this.splunkService = splunkService;
    }
    
    /**
     * Test Splunk connectivity
     */
    @Operation(summary = "Test Splunk connectivity", description = "Test connection to Splunk HEC endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connectivity test completed"),
            @ApiResponse(responseCode = "500", description = "Connectivity test failed")
    })
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        if (!splunkService.isConfigured()) {
            response.put("configured", false);
            response.put("message", "Splunk is not configured properly");
            response.put("status", "disabled");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        try {
            CompletableFuture<Boolean> testResult = splunkService.testConnection();
            boolean success = testResult.get(); // Wait for completion
            
            response.put("configured", true);
            response.put("connected", success);
            response.put("status", success ? "connected" : "connection_failed");
            response.put("message", success ? "Successfully connected to Splunk" : "Failed to connect to Splunk");
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            response.put("configured", true);
            response.put("connected", false);
            response.put("status", "error");
            response.put("message", "Error testing connection: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Send current metrics to Splunk
     */
    @Operation(summary = "Send metrics to Splunk", description = "Manually trigger sending application metrics to Splunk")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics sent successfully"),
            @ApiResponse(responseCode = "400", description = "Splunk not configured"),
            @ApiResponse(responseCode = "500", description = "Failed to send metrics")
    })
    @PostMapping("/metrics")
    public ResponseEntity<Map<String, Object>> sendMetrics() {
        Map<String, Object> response = new HashMap<>();
        
        if (!splunkService.isConfigured()) {
            response.put("success", false);
            response.put("message", "Splunk is not configured");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            CompletableFuture<Boolean> result = splunkService.sendMetrics();
            boolean success = result.get();
            
            response.put("success", success);
            response.put("message", success ? "Metrics sent successfully" : "Failed to send metrics");
            response.put("timestamp", java.time.Instant.now().toString());
            
            return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending metrics: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Send a custom event to Splunk
     */
    @Operation(summary = "Send custom event to Splunk", description = "Send a custom event with specified data to Splunk")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event sent successfully"),
            @ApiResponse(responseCode = "400", description = "Splunk not configured or invalid request"),
            @ApiResponse(responseCode = "500", description = "Failed to send event")
    })
    @PostMapping("/event")
    public ResponseEntity<Map<String, Object>> sendCustomEvent(@RequestBody Map<String, Object> eventData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!splunkService.isConfigured()) {
            response.put("success", false);
            response.put("message", "Splunk is not configured");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        if (eventData == null || eventData.isEmpty()) {
            response.put("success", false);
            response.put("message", "Event data is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            String eventType = (String) eventData.getOrDefault("event_type", "custom_event");
            eventData.put("source", "manual_api_call");
            eventData.put("timestamp", java.time.Instant.now().toString());
            
            CompletableFuture<Boolean> result = splunkService.sendEvent(eventType, eventData);
            boolean success = result.get();
            
            response.put("success", success);
            response.put("message", success ? "Event sent successfully" : "Failed to send event");
            response.put("event_type", eventType);
            response.put("timestamp", java.time.Instant.now().toString());
            
            return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending event: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get Splunk configuration status
     */
    @Operation(summary = "Get Splunk configuration status", description = "Get current Splunk configuration and status information")
    @ApiResponse(responseCode = "200", description = "Configuration status retrieved")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("configured", splunkService.isConfigured());
        response.put("timestamp", java.time.Instant.now().toString());
        
        if (splunkService.isConfigured()) {
            response.put("status", "enabled");
            response.put("message", "Splunk integration is configured and enabled");
        } else {
            response.put("status", "disabled");
            response.put("message", "Splunk integration is not configured. Check application properties.");
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Send a test error event to Splunk
     */
    @Operation(summary = "Send test error event", description = "Send a test error event to verify error logging functionality")
    @ApiResponse(responseCode = "200", description = "Test error event sent")
    @PostMapping("/test-error")
    public ResponseEntity<Map<String, Object>> sendTestError() {
        Map<String, Object> response = new HashMap<>();
        
        if (!splunkService.isConfigured()) {
            response.put("success", false);
            response.put("message", "Splunk is not configured");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("test_type", "manual_test");
            context.put("endpoint", "/api/splunk/test-error");
            context.put("source", "splunk_controller");
            
            String stackTrace = "Test stack trace:\n" +
                    "at com.example.springcrudh2.controller.SplunkController.sendTestError(SplunkController.java:xxx)\n" +
                    "at java.base/java.lang.reflect.Method.invoke(Method.java:xxx)";
            
            CompletableFuture<Boolean> result = splunkService.sendErrorEvent(
                    "TestException", 
                    "This is a test error event from the Splunk controller", 
                    stackTrace, 
                    context
            );
            
            boolean success = result.get();
            
            response.put("success", success);
            response.put("message", success ? "Test error event sent successfully" : "Failed to send test error event");
            response.put("timestamp", java.time.Instant.now().toString());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending test error event: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}