package com.example.springcrudh2.interceptor;

import com.example.springcrudh2.service.SplunkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Interceptor for logging HTTP requests and responses to Splunk
 * Captures performance metrics, security events, and user activities
 */
@Component
public class SplunkLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = Logger.getLogger(SplunkLoggingInterceptor.class.getName());
    
    private final SplunkService splunkService;
    
    // Store request start times for performance measurement
    private final Map<String, Long> requestStartTimes = new ConcurrentHashMap<>();
    
    @Autowired
    public SplunkLoggingInterceptor(SplunkService splunkService) {
        this.splunkService = splunkService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = generateRequestId(request);
        long startTime = System.currentTimeMillis();
        
        // Store start time for performance measurement
        requestStartTimes.put(requestId, startTime);
        
        // Log request details to Splunk
        if (splunkService.isConfigured()) {
            Map<String, Object> requestData = extractRequestData(request, handler);
            requestData.put("request_id", requestId);
            requestData.put("phase", "request_start");
            
            splunkService.sendEvent("http_request", requestData);
            
            // Check for potential security events
            checkSecurityEvents(request);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestId = generateRequestId(request);
        Long startTime = requestStartTimes.remove(requestId);
        
        if (startTime != null && splunkService.isConfigured()) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log performance metrics
            Map<String, Object> responseData = extractResponseData(request, response, handler, duration, ex);
            responseData.put("request_id", requestId);
            responseData.put("phase", "request_complete");
            
            splunkService.sendEvent("http_response", responseData);
            
            // Send performance event
            String endpoint = request.getRequestURI();
            Map<String, Object> perfDetails = new HashMap<>();
            perfDetails.put("method", request.getMethod());
            perfDetails.put("user_agent", request.getHeader("User-Agent"));
            perfDetails.put("request_size", request.getContentLength());
            perfDetails.put("response_size", response.getBufferSize());
            
            splunkService.sendPerformanceEvent(endpoint, duration, response.getStatus(), perfDetails);
            
            // Log errors if any
            if (ex != null) {
                logException(request, response, ex);
            }
            
            // Log slow requests
            if (duration > 5000) { // 5 seconds threshold
                logSlowRequest(request, response, duration);
            }
        }
    }
    
    /**
     * Extract request data for logging
     */
    private Map<String, Object> extractRequestData(HttpServletRequest request, Object handler) {
        Map<String, Object> data = new HashMap<>();
        
        // Basic request info
        data.put("method", request.getMethod());
        data.put("uri", request.getRequestURI());
        data.put("query_string", request.getQueryString());
        data.put("protocol", request.getProtocol());
        data.put("remote_addr", getClientIpAddress(request));
        data.put("remote_host", request.getRemoteHost());
        data.put("server_name", request.getServerName());
        data.put("server_port", request.getServerPort());
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Don't log sensitive headers
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        data.put("headers", headers);
        
        // Handler info
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            data.put("controller", handlerMethod.getBeanType().getSimpleName());
            data.put("method_name", handlerMethod.getMethod().getName());
        }
        
        // Session info
        if (request.getSession(false) != null) {
            data.put("session_id", request.getSession().getId());
        }
        
        // Content info
        data.put("content_type", request.getContentType());
        data.put("content_length", request.getContentLength());
        data.put("character_encoding", request.getCharacterEncoding());
        
        return data;
    }
    
    /**
     * Extract response data for logging
     */
    private Map<String, Object> extractResponseData(HttpServletRequest request, HttpServletResponse response, 
                                                   Object handler, long duration, Exception ex) {
        Map<String, Object> data = new HashMap<>();
        
        // Copy request data
        data.putAll(extractRequestData(request, handler));
        
        // Response info
        data.put("status_code", response.getStatus());
        data.put("content_type", response.getContentType());
        data.put("character_encoding", response.getCharacterEncoding());
        data.put("duration_ms", duration);
        
        // Response headers
        Map<String, String> responseHeaders = new HashMap<>();
        for (String headerName : response.getHeaderNames()) {
            if (!isSensitiveHeader(headerName)) {
                responseHeaders.put(headerName, response.getHeader(headerName));
            }
        }
        data.put("response_headers", responseHeaders);
        
        // Exception info
        if (ex != null) {
            data.put("exception", ex.getClass().getSimpleName());
            data.put("exception_message", ex.getMessage());
        }
        
        return data;
    }
    
    /**
     * Check for potential security events
     */
    private void checkSecurityEvents(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String sourceIp = getClientIpAddress(request);
        
        Map<String, Object> securityDetails = new HashMap<>();
        securityDetails.put("method", request.getMethod());
        securityDetails.put("uri", request.getRequestURI());
        securityDetails.put("query_string", request.getQueryString());
        
        // Check for suspicious patterns
        if (isSuspiciousRequest(request)) {
            splunkService.sendSecurityEvent("suspicious_request", sourceIp, userAgent, securityDetails);
        }
        
        // Check for common attack patterns
        String uri = request.getRequestURI().toLowerCase();
        String queryString = request.getQueryString();
        
        if (containsSqlInjectionPattern(uri, queryString)) {
            securityDetails.put("attack_type", "sql_injection");
            splunkService.sendSecurityEvent("sql_injection_attempt", sourceIp, userAgent, securityDetails);
        }
        
        if (containsXssPattern(uri, queryString)) {
            securityDetails.put("attack_type", "xss");
            splunkService.sendSecurityEvent("xss_attempt", sourceIp, userAgent, securityDetails);
        }
        
        if (containsDirectoryTraversalPattern(uri)) {
            securityDetails.put("attack_type", "directory_traversal");
            splunkService.sendSecurityEvent("directory_traversal_attempt", sourceIp, userAgent, securityDetails);
        }
    }
    
    /**
     * Log exceptions to Splunk
     */
    private void logException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Map<String, Object> context = new HashMap<>();
        context.put("method", request.getMethod());
        context.put("uri", request.getRequestURI());
        context.put("status_code", response.getStatus());
        context.put("user_agent", request.getHeader("User-Agent"));
        context.put("source_ip", getClientIpAddress(request));
        
        String stackTrace = getStackTrace(ex);
        splunkService.sendErrorEvent(ex.getClass().getSimpleName(), ex.getMessage(), stackTrace, context);
    }
    
    /**
     * Log slow requests to Splunk
     */
    private void logSlowRequest(HttpServletRequest request, HttpServletResponse response, long duration) {
        Map<String, Object> slowRequestData = new HashMap<>();
        slowRequestData.put("method", request.getMethod());
        slowRequestData.put("uri", request.getRequestURI());
        slowRequestData.put("duration_ms", duration);
        slowRequestData.put("status_code", response.getStatus());
        slowRequestData.put("user_agent", request.getHeader("User-Agent"));
        slowRequestData.put("source_ip", getClientIpAddress(request));
        slowRequestData.put("severity", "WARNING");
        
        splunkService.sendEvent("slow_request", slowRequestData);
    }
    
    /**
     * Generate unique request ID
     */
    private String generateRequestId(HttpServletRequest request) {
        return request.getMethod() + "_" + request.hashCode() + "_" + System.currentTimeMillis();
    }
    
    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Check if header is sensitive and should not be logged
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") || 
               lowerName.contains("password") || 
               lowerName.contains("token") ||
               lowerName.contains("cookie") ||
               lowerName.contains("secret");
    }
    
    /**
     * Check if request looks suspicious
     */
    private boolean isSuspiciousRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        
        // Check for bot/scanner user agents
        if (userAgent != null) {
            String lowerUserAgent = userAgent.toLowerCase();
            if (lowerUserAgent.contains("bot") || 
                lowerUserAgent.contains("scanner") || 
                lowerUserAgent.contains("crawler") ||
                lowerUserAgent.contains("spider")) {
                return true;
            }
        }
        
        // Check for unusual request patterns
        String uri = request.getRequestURI();
        if (uri.contains("..") || uri.contains("script") || uri.contains("union")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check for SQL injection patterns
     */
    private boolean containsSqlInjectionPattern(String uri, String queryString) {
        String content = (uri + " " + (queryString != null ? queryString : "")).toLowerCase();
        return content.contains("union") || content.contains("select") || 
               content.contains("insert") || content.contains("delete") ||
               content.contains("drop") || content.contains("'or'") ||
               content.contains("'and'") || content.contains("1=1");
    }
    
    /**
     * Check for XSS patterns
     */
    private boolean containsXssPattern(String uri, String queryString) {
        String content = (uri + " " + (queryString != null ? queryString : "")).toLowerCase();
        return content.contains("<script") || content.contains("javascript:") ||
               content.contains("onload=") || content.contains("onerror=") ||
               content.contains("alert(") || content.contains("eval(");
    }
    
    /**
     * Check for directory traversal patterns
     */
    private boolean containsDirectoryTraversalPattern(String uri) {
        return uri.contains("../") || uri.contains("..\\") ||
               uri.contains("/etc/passwd") || uri.contains("\\windows\\");
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception ex) {
        if (ex == null) return "";
        
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}