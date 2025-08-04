package com.metrics.light;

import java.util.Map;
import java.util.HashMap;

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