package com.metrics.light;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Contains details extracted from a curl command for HTTP requests.
 */
public class RequestDetails {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    
    public RequestDetails(String url, String method, Map<String, String> headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>(headers);
        this.body = body;
        
        // Handle special correlation ID header
        processCorrelationId();
    }
    
    private void processCorrelationId() {
        // Look for the correlation ID header (case-insensitive)
        String correlationKey = null;
        for (String key : headers.keySet()) {
            if ("one-data-correlation-id".equalsIgnoreCase(key)) {
                correlationKey = key;
                break;
            }
        }
        
        // If found, replace with unique value for each request
        if (correlationKey != null) {
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            headers.put(correlationKey, "CSSLOADTEST" + uuid);
        }
    }
    
    /**
     * Creates a new RequestDetails with a fresh correlation ID for each request.
     */
    public RequestDetails withFreshCorrelationId() {
        return new RequestDetails(url, method, headers, body);
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getMethod() {
        return method;
    }
    
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
    
    public String getBody() {
        return body;
    }
    
    @Override
    public String toString() {
        return String.format("RequestDetails{url='%s', method='%s', headers=%s, hasBody=%s}", 
                url, method, headers, body != null);
    }
}