package com.example.springcrudh2.scheduler;

import com.example.springcrudh2.config.SplunkConfig;
import com.example.springcrudh2.service.SplunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduler for automatically sending metrics to Splunk
 */
@Component
@ConditionalOnProperty(value = "splunk.enabled", havingValue = "true")
public class SplunkMetricsScheduler {
    
    private static final Logger logger = Logger.getLogger(SplunkMetricsScheduler.class.getName());
    
    private final SplunkService splunkService;
    private final SplunkConfig.SplunkProperties splunkProperties;
    
    @Autowired
    public SplunkMetricsScheduler(SplunkService splunkService, SplunkConfig.SplunkProperties splunkProperties) {
        this.splunkService = splunkService;
        this.splunkProperties = splunkProperties;
    }
    
    /**
     * Send metrics to Splunk at configured intervals
     * Default: every 60 seconds
     */
    @Scheduled(fixedRateString = "${splunk.metrics.interval-seconds:60}000")
    public void sendMetricsToSplunk() {
        if (!splunkService.isConfigured() || !splunkProperties.getMetrics().isEnabled()) {
            return;
        }
        
        try {
            logger.fine("Sending scheduled metrics to Splunk");
            
            splunkService.sendMetrics().thenAccept(success -> {
                if (success) {
                    logger.fine("Successfully sent scheduled metrics to Splunk");
                } else {
                    logger.warning("Failed to send scheduled metrics to Splunk");
                }
            }).exceptionally(throwable -> {
                logger.log(Level.WARNING, "Error sending scheduled metrics to Splunk", throwable);
                return null;
            });
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected error in metrics scheduler", e);
        }
    }
    
    /**
     * Send heartbeat event to Splunk every 5 minutes
     * This helps monitor application health in Splunk
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void sendHeartbeat() {
        if (!splunkService.isConfigured()) {
            return;
        }
        
        try {
            java.util.Map<String, Object> heartbeatData = new java.util.HashMap<>();
            heartbeatData.put("application_status", "running");
            heartbeatData.put("timestamp", java.time.Instant.now().toString());
            heartbeatData.put("uptime_ms", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime());
            heartbeatData.put("available_processors", Runtime.getRuntime().availableProcessors());
            heartbeatData.put("free_memory", Runtime.getRuntime().freeMemory());
            heartbeatData.put("total_memory", Runtime.getRuntime().totalMemory());
            heartbeatData.put("max_memory", Runtime.getRuntime().maxMemory());
            
            splunkService.sendEvent("application_heartbeat", heartbeatData).thenAccept(success -> {
                if (success) {
                    logger.fine("Successfully sent heartbeat to Splunk");
                } else {
                    logger.fine("Failed to send heartbeat to Splunk (this is normal if Splunk is not configured)");
                }
            });
            
        } catch (Exception e) {
            logger.log(Level.FINE, "Error sending heartbeat to Splunk", e);
        }
    }
}