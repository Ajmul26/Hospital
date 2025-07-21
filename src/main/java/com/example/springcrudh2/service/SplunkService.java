package com.example.springcrudh2.service;

import com.example.springcrudh2.config.SplunkConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for sending events and metrics to Splunk
 * Handles HTTP Event Collector (HEC) communication
 */
@Service
public class SplunkService {
    
    private static final Logger logger = Logger.getLogger(SplunkService.class.getName());
    
    private final SplunkConfig.SplunkProperties splunkProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final MetricsEndpoint metricsEndpoint;
    
    @Autowired
    public SplunkService(SplunkConfig.SplunkProperties splunkProperties, 
                        ObjectMapper objectMapper,
                        MetricsEndpoint metricsEndpoint) {
        this.splunkProperties = splunkProperties;
        this.objectMapper = objectMapper;
        this.metricsEndpoint = metricsEndpoint;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    /**
     * Send a generic event to Splunk
     */
    public CompletableFuture<Boolean> sendEvent(String eventType, Object data) {
        if (!splunkProperties.isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> event = createBaseEvent(eventType);
                event.put("data", data);
                
                return sendToSplunk(event);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send event to Splunk", e);
                return false;
            }
        });
    }
    
    /**
     * Send user operation event to Splunk
     */
    public CompletableFuture<Boolean> sendUserEvent(String operation, Long userId, String userEmail, Map<String, Object> details) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("operation", operation);
        eventData.put("user_id", userId);
        eventData.put("user_email", userEmail);
        eventData.put("details", details);
        eventData.put("timestamp", Instant.now().toString());
        
        return sendEvent("user_operation", eventData);
    }
    
    /**
     * Send application metrics to Splunk
     */
    public CompletableFuture<Boolean> sendMetrics() {
        if (!splunkProperties.isEnabled() || !splunkProperties.getMetrics().isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> metrics = collectMetrics();
                Map<String, Object> event = createBaseEvent("application_metrics");
                event.put("metrics", metrics);
                
                return sendToSplunk(event);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send metrics to Splunk", e);
                return false;
            }
        });
    }
    
    /**
     * Send error event to Splunk
     */
    public CompletableFuture<Boolean> sendErrorEvent(String errorType, String message, String stackTrace, Map<String, Object> context) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("error_type", errorType);
        errorData.put("message", message);
        errorData.put("stack_trace", stackTrace);
        errorData.put("context", context);
        errorData.put("timestamp", Instant.now().toString());
        errorData.put("severity", "ERROR");
        
        return sendEvent("application_error", errorData);
    }
    
    /**
     * Send performance event to Splunk
     */
    public CompletableFuture<Boolean> sendPerformanceEvent(String endpoint, long duration, int statusCode, Map<String, Object> details) {
        Map<String, Object> perfData = new HashMap<>();
        perfData.put("endpoint", endpoint);
        perfData.put("duration_ms", duration);
        perfData.put("status_code", statusCode);
        perfData.put("details", details);
        perfData.put("timestamp", Instant.now().toString());
        
        return sendEvent("performance", perfData);
    }
    
    /**
     * Send security event to Splunk
     */
    public CompletableFuture<Boolean> sendSecurityEvent(String eventType, String sourceIp, String userAgent, Map<String, Object> details) {
        Map<String, Object> securityData = new HashMap<>();
        securityData.put("security_event_type", eventType);
        securityData.put("source_ip", sourceIp);
        securityData.put("user_agent", userAgent);
        securityData.put("details", details);
        securityData.put("timestamp", Instant.now().toString());
        securityData.put("severity", "SECURITY");
        
        return sendEvent("security_event", securityData);
    }
    
    /**
     * Create base event structure
     */
    private Map<String, Object> createBaseEvent(String eventType) {
        Map<String, Object> event = new HashMap<>();
        event.put("time", Instant.now().getEpochSecond());
        event.put("event_type", eventType);
        event.put("application", "spring-boot-user-management");
        event.put("environment", getEnvironment());
        event.put("version", getApplicationVersion());
        
        return event;
    }
    
    /**
     * Send event to Splunk HEC
     */
    private boolean sendToSplunk(Map<String, Object> eventData) {
        try {
            // Create Splunk HEC event format
            ObjectNode hecEvent = objectMapper.createObjectNode();
            hecEvent.put("time", Instant.now().getEpochSecond());
            hecEvent.put("index", splunkProperties.getIndex());
            hecEvent.put("source", splunkProperties.getSource());
            hecEvent.put("sourcetype", splunkProperties.getSourcetype());
            hecEvent.set("event", objectMapper.valueToTree(eventData));
            
            String jsonPayload = objectMapper.writeValueAsString(hecEvent);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(splunkProperties.getHec().getUrl() + "/services/collector"))
                    .header("Authorization", "Splunk " + splunkProperties.getHec().getToken())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.fine("Successfully sent event to Splunk");
                return true;
            } else {
                logger.warning("Failed to send event to Splunk. Status: " + response.statusCode() + ", Body: " + response.body());
                return false;
            }
            
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error sending event to Splunk", e);
            return false;
        }
    }
    
    /**
     * Collect application metrics
     */
    private Map<String, Object> collectMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Collect JVM metrics
            metrics.put("jvm", collectJvmMetrics());
            
            // Collect HTTP metrics
            metrics.put("http", collectHttpMetrics());
            
            // Collect application metrics
            metrics.put("application", collectApplicationMetrics());
            
            // Add timestamp
            metrics.put("timestamp", Instant.now().toString());
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error collecting metrics", e);
        }
        
        return metrics;
    }
    
    /**
     * Collect JVM metrics
     */
    private Map<String, Object> collectJvmMetrics() {
        Map<String, Object> jvmMetrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        jvmMetrics.put("memory_total", runtime.totalMemory());
        jvmMetrics.put("memory_free", runtime.freeMemory());
        jvmMetrics.put("memory_used", runtime.totalMemory() - runtime.freeMemory());
        jvmMetrics.put("memory_max", runtime.maxMemory());
        jvmMetrics.put("processors", runtime.availableProcessors());
        
        return jvmMetrics;
    }
    
    /**
     * Collect HTTP metrics
     */
    private Map<String, Object> collectHttpMetrics() {
        Map<String, Object> httpMetrics = new HashMap<>();
        
        try {
            // Use Micrometer metrics if available
            var httpRequestsMetric = metricsEndpoint.metric("http.server.requests", null);
            if (httpRequestsMetric != null) {
                httpMetrics.put("requests_total", httpRequestsMetric.getMeasurements());
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "HTTP metrics not available", e);
        }
        
        return httpMetrics;
    }
    
    /**
     * Collect custom application metrics
     */
    private Map<String, Object> collectApplicationMetrics() {
        Map<String, Object> appMetrics = new HashMap<>();
        
        // Add custom application metrics here
        appMetrics.put("uptime", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime());
        appMetrics.put("active_profiles", getEnvironment());
        
        return appMetrics;
    }
    
    /**
     * Get current environment
     */
    private String getEnvironment() {
        return System.getProperty("spring.profiles.active", "default");
    }
    
    /**
     * Get application version
     */
    private String getApplicationVersion() {
        return getClass().getPackage().getImplementationVersion() != null ? 
               getClass().getPackage().getImplementationVersion() : "1.0.0";
    }
    
    /**
     * Check if Splunk is enabled and configured
     */
    public boolean isConfigured() {
        return splunkProperties.isEnabled() && 
               !splunkProperties.getHec().getToken().isEmpty() &&
               !splunkProperties.getHec().getUrl().isEmpty();
    }
    
    /**
     * Test Splunk connectivity
     */
    public CompletableFuture<Boolean> testConnection() {
        if (!isConfigured()) {
            return CompletableFuture.completedFuture(false);
        }
        
        Map<String, Object> testData = new HashMap<>();
        testData.put("test", "connectivity_check");
        testData.put("timestamp", Instant.now().toString());
        
        return sendEvent("connectivity_test", testData);
    }
}