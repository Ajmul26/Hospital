# Splunk Integration for Spring Boot User Management Application

This document provides comprehensive information about the Splunk integration in the Spring Boot User Management application, including setup, configuration, and monitoring capabilities.

## 🔍 Overview

The application includes comprehensive Splunk integration for:

- **Centralized Logging**: All application logs sent to Splunk
- **Performance Monitoring**: HTTP request/response metrics
- **Security Event Tracking**: Suspicious activity and attack detection
- **User Activity Logging**: CRUD operations on users
- **Application Health Monitoring**: Heartbeat and system metrics
- **Error Tracking**: Exception and error event logging

## 🏗️ Architecture

### Components

1. **SplunkService**: Core service for sending events to Splunk HEC
2. **SplunkLoggingInterceptor**: HTTP request/response interceptor
3. **SplunkMetricsScheduler**: Scheduled metrics collection
4. **SplunkController**: API endpoints for Splunk management
5. **Logback Integration**: Direct log streaming to Splunk

### Event Types

- `user_operation`: User CRUD operations (Create, Update, Delete)
- `http_request`: HTTP request details
- `http_response`: HTTP response with performance metrics
- `performance`: Endpoint performance metrics
- `security_event`: Security-related events (SQL injection, XSS attempts)
- `application_error`: Exception and error tracking
- `application_metrics`: System and JVM metrics
- `application_heartbeat`: Health monitoring
- `slow_request`: Requests exceeding performance thresholds

## 🚀 Quick Start

### 1. Prerequisites

- **Splunk Enterprise or Splunk Cloud** instance
- **HTTP Event Collector (HEC)** enabled in Splunk
- **HEC Token** generated in Splunk

### 2. Enable Splunk in Application

Edit `src/main/resources/application.properties`:

```properties
# Enable Splunk integration
splunk.enabled=true

# Configure HEC endpoint
splunk.hec.url=http://your-splunk-server:8088
splunk.hec.token=your-hec-token-here

# Configure index and source
splunk.index=your_index_name
splunk.source=spring-boot-user-management
splunk.sourcetype=spring-boot
```

### 3. Test Integration

```bash
# Start the application
mvn spring-boot:run

# Test Splunk connectivity
curl http://localhost:8080/api/splunk/test

# Send test metrics
curl -X POST http://localhost:8080/api/splunk/metrics

# Send custom event
curl -X POST http://localhost:8080/api/splunk/event \
  -H "Content-Type: application/json" \
  -d '{"event_type": "test_event", "message": "Hello Splunk!"}'
```

## ⚙️ Configuration

### Complete Configuration Options

```properties
# Enable/Disable Splunk
splunk.enabled=true

# HTTP Event Collector Configuration
splunk.hec.url=http://your-splunk-server:8088
splunk.hec.token=your-hec-token-here
splunk.hec.disable-certificate-validation=false
splunk.hec.batch-interval=10000
splunk.hec.batch-size=100
splunk.hec.batch-count=10
splunk.hec.retry-count=3

# Splunk Index and Source Settings
splunk.index=main
splunk.source=spring-boot-user-management
splunk.sourcetype=spring-boot

# Metrics Configuration
splunk.metrics.enabled=true
splunk.metrics.interval-seconds=60
splunk.metrics.prefix=spring.boot.app

# Actuator Configuration for Metrics
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
management.metrics.export.prometheus.enabled=true
```

### Environment-Specific Configuration

#### Development
```properties
splunk.enabled=false
# Logs only to console and files
```

#### Staging
```properties
splunk.enabled=true
splunk.hec.url=http://staging-splunk:8088
splunk.index=staging_index
splunk.metrics.interval-seconds=30
```

#### Production
```properties
splunk.enabled=true
splunk.hec.url=https://prod-splunk:8088
splunk.index=production_index
splunk.metrics.interval-seconds=60
splunk.hec.disable-certificate-validation=false
```

## 🔧 Splunk Setup

### 1. Enable HTTP Event Collector

1. Log into Splunk Web
2. Go to **Settings > Data Inputs**
3. Click **HTTP Event Collector**
4. Click **New Token**
5. Configure the token:
   - **Name**: spring-boot-user-management
   - **Source name override**: spring-boot-user-management
   - **Index**: Select or create an index (e.g., `applications`)
6. Save and note the token value

### 2. Create Index (Optional)

```spl
# In Splunk CLI or Web
./splunk add index applications -homePath $SPLUNK_DB/applications/db -coldPath $SPLUNK_DB/applications/colddb -thawedPath $SPLUNK_DB/applications/thaweddb
```

### 3. Configure Index Retention

```spl
# Set retention policy
[applications]
homePath = $SPLUNK_DB/applications/db
coldPath = $SPLUNK_DB/applications/colddb
thawedPath = $SPLUNK_DB/applications/thaweddb
maxDataSize = auto_high_volume
maxHotBuckets = 10
maxWarmDBCount = 300
maxTotalDataSizeMB = 500000
```

## 📊 Monitoring and Dashboards

### Key Searches

#### User Activity
```spl
index=applications sourcetype=spring-boot event_type=user_operation
| timechart span=1h count by operation
| eval CREATE=coalesce(CREATE,0), UPDATE=coalesce(UPDATE,0), DELETE=coalesce(DELETE,0)
```

#### Performance Monitoring
```spl
index=applications sourcetype=spring-boot event_type=performance
| eval response_time_sec=duration_ms/1000
| timechart span=5m avg(response_time_sec) as avg_response_time, max(response_time_sec) as max_response_time by endpoint
```

#### Error Tracking
```spl
index=applications sourcetype=spring-boot event_type=application_error
| timechart span=1h count by error_type
| sort -_time
```

#### Security Events
```spl
index=applications sourcetype=spring-boot event_type=security_event
| timechart span=1h count by security_event_type
| sort -_time
```

#### System Health
```spl
index=applications sourcetype=spring-boot event_type=application_heartbeat
| eval memory_usage_pct=(total_memory-free_memory)/total_memory*100
| timechart span=5m avg(memory_usage_pct) as memory_usage
```

### Sample Dashboard Panels

#### Application Overview
```xml
<dashboard>
  <label>Spring Boot User Management</label>
  <row>
    <panel>
      <title>Request Volume</title>
      <chart>
        <search>
          <query>
            index=applications sourcetype=spring-boot event_type=http_response
            | timechart span=5m count as requests
          </query>
        </search>
      </chart>
    </panel>
    <panel>
      <title>Response Times</title>
      <chart>
        <search>
          <query>
            index=applications sourcetype=spring-boot event_type=performance
            | eval response_time_sec=duration_ms/1000
            | timechart span=5m avg(response_time_sec) as avg_response_time
          </query>
        </search>
      </chart>
    </panel>
  </row>
</dashboard>
```

## 🔍 Log Analysis

### Log Levels and Content

#### User Operations
```json
{
  "time": 1699123456,
  "event_type": "user_operation",
  "application": "spring-boot-user-management",
  "data": {
    "operation": "CREATE",
    "user_id": 123,
    "user_email": "john.doe@example.com",
    "details": {
      "user_name": "John Doe",
      "user_phone": "+1234567890"
    }
  }
}
```

#### Performance Events
```json
{
  "time": 1699123456,
  "event_type": "performance",
  "application": "spring-boot-user-management",
  "data": {
    "endpoint": "/api/users",
    "duration_ms": 150,
    "status_code": 200,
    "details": {
      "method": "GET",
      "user_agent": "Mozilla/5.0...",
      "request_size": 0,
      "response_size": 1024
    }
  }
}
```

#### Security Events
```json
{
  "time": 1699123456,
  "event_type": "security_event",
  "application": "spring-boot-user-management",
  "data": {
    "security_event_type": "sql_injection_attempt",
    "source_ip": "192.168.1.100",
    "user_agent": "curl/7.68.0",
    "details": {
      "attack_type": "sql_injection",
      "method": "GET",
      "uri": "/api/users?id=1 OR 1=1",
      "query_string": "id=1 OR 1=1"
    }
  }
}
```

## 🛡️ Security Features

### Automatic Threat Detection

The application automatically detects and logs:

1. **SQL Injection Attempts**
   - Pattern matching for SQL keywords
   - Suspicious query parameters

2. **XSS Attempts**
   - Script injection detection
   - Event handler detection

3. **Directory Traversal**
   - Path traversal patterns
   - File access attempts

4. **Suspicious User Agents**
   - Bot detection
   - Scanner identification

### Security Event Configuration

```java
// Custom security rules can be added in SplunkLoggingInterceptor
private boolean containsSqlInjectionPattern(String uri, String queryString) {
    String content = (uri + " " + (queryString != null ? queryString : "")).toLowerCase();
    return content.contains("union") || content.contains("select") || 
           content.contains("insert") || content.contains("delete");
}
```

## 📈 Performance Monitoring

### Key Metrics Collected

1. **HTTP Metrics**
   - Request count by endpoint
   - Response times
   - Status code distribution
   - Request/response sizes

2. **JVM Metrics**
   - Memory usage (heap, non-heap)
   - Garbage collection statistics
   - Thread counts
   - CPU usage

3. **Application Metrics**
   - Database connection pool status
   - Cache hit rates
   - Custom business metrics

### Performance Thresholds

```properties
# Configure performance monitoring thresholds
app.performance.slow-request-threshold=5000  # 5 seconds
app.performance.error-rate-threshold=5       # 5% error rate
app.performance.memory-threshold=80           # 80% memory usage
```

## 🔧 API Endpoints

### Splunk Management API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/splunk/status` | GET | Get Splunk configuration status |
| `/api/splunk/test` | GET | Test Splunk connectivity |
| `/api/splunk/metrics` | POST | Send current metrics to Splunk |
| `/api/splunk/event` | POST | Send custom event to Splunk |
| `/api/splunk/test-error` | POST | Send test error event |

### Example Usage

```bash
# Check Splunk status
curl http://localhost:8080/api/splunk/status

# Test connectivity
curl http://localhost:8080/api/splunk/test

# Send metrics manually
curl -X POST http://localhost:8080/api/splunk/metrics

# Send custom event
curl -X POST http://localhost:8080/api/splunk/event \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "custom_business_event",
    "user_action": "bulk_export",
    "record_count": 1000,
    "export_format": "csv"
  }'
```

## 🧪 Testing

### Manual Testing

1. **Test User Operations**
   ```bash
   # Create user and check Splunk logs
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"name": "Test User", "email": "test@example.com"}'
   ```

2. **Test Security Detection**
   ```bash
   # Trigger SQL injection detection
   curl "http://localhost:8080/api/users?id=1 OR 1=1"
   
   # Trigger XSS detection
   curl "http://localhost:8080/api/users?search=<script>alert('xss')</script>"
   ```

3. **Test Performance Monitoring**
   ```bash
   # Generate load for performance metrics
   for i in {1..100}; do
     curl http://localhost:8080/api/users &
   done
   wait
   ```

### Automated Testing

```java
@Test
public void testSplunkEventSending() {
    // Test that events are properly formatted and sent
    Map<String, Object> testData = Map.of("test", "value");
    CompletableFuture<Boolean> result = splunkService.sendEvent("test_event", testData);
    assertTrue(result.join());
}
```

## 🔍 Troubleshooting

### Common Issues

1. **Connection Failures**
   ```
   Error: Failed to connect to Splunk HEC
   Solution: Check network connectivity, HEC URL, and token
   ```

2. **Index Not Found**
   ```
   Error: Invalid index specified
   Solution: Create index in Splunk or use existing index
   ```

3. **Authentication Errors**
   ```
   Error: Invalid HEC token
   Solution: Verify token is correct and has proper permissions
   ```

### Debug Configuration

```properties
# Enable debug logging for Splunk components
logging.level.com.example.springcrudh2.service.SplunkService=DEBUG
logging.level.com.example.springcrudh2.interceptor.SplunkLoggingInterceptor=DEBUG
```

### Health Checks

```bash
# Check if Splunk is receiving events
curl http://localhost:8080/api/splunk/test

# Check application metrics
curl http://localhost:8080/actuator/metrics

# Check health endpoint
curl http://localhost:8080/actuator/health
```

## 📝 Best Practices

### Configuration
- Use environment variables for sensitive data
- Configure appropriate retention policies
- Set up proper index hierarchy
- Monitor HEC quotas and limits

### Performance
- Use appropriate batch sizes for HEC
- Configure retry policies for reliability
- Monitor network latency to Splunk
- Implement circuit breakers for resilience

### Security
- Use HTTPS for HEC communication
- Rotate HEC tokens regularly
- Limit sensitive data in logs
- Implement proper access controls

### Monitoring
- Set up alerts for application errors
- Monitor Splunk ingestion rates
- Track application performance trends
- Create operational dashboards

## 🔄 Maintenance

### Regular Tasks

1. **Token Rotation**
   - Generate new HEC tokens quarterly
   - Update application configuration
   - Test connectivity after rotation

2. **Index Maintenance**
   - Monitor index size and retention
   - Archive old data as needed
   - Optimize search performance

3. **Dashboard Updates**
   - Review and update dashboards
   - Add new metrics as needed
   - Remove obsolete visualizations

### Monitoring Splunk Integration

```spl
# Monitor HEC ingestion
index=_internal source=*splunkd.log component=HttpInputDataHandler
| timechart span=5m count as events_received

# Check for errors
index=_internal source=*splunkd.log level=ERROR component=HttpInputDataHandler
| table _time, message
```

## 📞 Support

### Debugging Steps

1. Check application logs for Splunk-related errors
2. Verify HEC endpoint accessibility
3. Validate token permissions
4. Test with simple curl commands
5. Check Splunk internal logs

### Contact Information

For issues related to:
- **Application Integration**: Check application logs and configuration
- **Splunk Configuration**: Consult Splunk documentation
- **Network Connectivity**: Contact network administrators
- **Performance Issues**: Monitor metrics and adjust configuration

## 📚 Additional Resources

- [Splunk HTTP Event Collector Documentation](https://docs.splunk.com/Documentation/Splunk/latest/Data/UsetheHTTPEventCollector)
- [Spring Boot Actuator Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Logback Configuration](http://logback.qos.ch/manual/configuration.html)
- [Micrometer Metrics](https://micrometer.io/docs)