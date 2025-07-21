package com.example.springcrudh2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * Splunk Configuration for logging and monitoring
 * Configures Splunk HTTP Event Collector (HEC) integration
 */
@Configuration
public class SplunkConfig {
    
    private static final Logger logger = Logger.getLogger(SplunkConfig.class.getName());
    
    @Value("${splunk.hec.url:http://localhost:8088}")
    private String splunkHecUrl;
    
    @Value("${splunk.hec.token:}")
    private String splunkHecToken;
    
    @Value("${splunk.index:main}")
    private String splunkIndex;
    
    @Value("${splunk.source:spring-boot-app}")
    private String splunkSource;
    
    @Value("${splunk.sourcetype:spring-boot}")
    private String splunkSourceType;
    
    @Value("${splunk.enabled:false}")
    private boolean splunkEnabled;
    
    @PostConstruct
    public void init() {
        if (splunkEnabled) {
            logger.info("Splunk logging enabled");
            logger.info("Splunk HEC URL: " + splunkHecUrl);
            logger.info("Splunk Index: " + splunkIndex);
            logger.info("Splunk Source: " + splunkSource);
            logger.info("Splunk SourceType: " + splunkSourceType);
        } else {
            logger.info("Splunk logging disabled - use splunk.enabled=true to enable");
        }
    }
    
    /**
     * Splunk Properties Configuration
     */
    @Component
    @ConfigurationProperties(prefix = "splunk")
    public static class SplunkProperties {
        private Hec hec = new Hec();
        private String index = "main";
        private String source = "spring-boot-app";
        private String sourcetype = "spring-boot";
        private boolean enabled = false;
        private Metrics metrics = new Metrics();
        
        // Getters and Setters
        public Hec getHec() { return hec; }
        public void setHec(Hec hec) { this.hec = hec; }
        
        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public String getSourcetype() { return sourcetype; }
        public void setSourcetype(String sourcetype) { this.sourcetype = sourcetype; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public Metrics getMetrics() { return metrics; }
        public void setMetrics(Metrics metrics) { this.metrics = metrics; }
        
        /**
         * HTTP Event Collector Configuration
         */
        public static class Hec {
            private String url = "http://localhost:8088";
            private String token = "";
            private boolean disableCertificateValidation = false;
            private int batchInterval = 10000; // 10 seconds
            private int batchSize = 100;
            private int batchCount = 10;
            private int retryCount = 3;
            
            // Getters and Setters
            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
            
            public String getToken() { return token; }
            public void setToken(String token) { this.token = token; }
            
            public boolean isDisableCertificateValidation() { return disableCertificateValidation; }
            public void setDisableCertificateValidation(boolean disableCertificateValidation) { 
                this.disableCertificateValidation = disableCertificateValidation; 
            }
            
            public int getBatchInterval() { return batchInterval; }
            public void setBatchInterval(int batchInterval) { this.batchInterval = batchInterval; }
            
            public int getBatchSize() { return batchSize; }
            public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
            
            public int getBatchCount() { return batchCount; }
            public void setBatchCount(int batchCount) { this.batchCount = batchCount; }
            
            public int getRetryCount() { return retryCount; }
            public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        }
        
        /**
         * Metrics Configuration
         */
        public static class Metrics {
            private boolean enabled = true;
            private int intervalSeconds = 60;
            private String prefix = "spring.boot.app";
            
            // Getters and Setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            
            public int getIntervalSeconds() { return intervalSeconds; }
            public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }
            
            public String getPrefix() { return prefix; }
            public void setPrefix(String prefix) { this.prefix = prefix; }
        }
    }
}