package com.metrics.light;

/**
 * Represents an HTTP response with status code and success indicator.
 */
public class HttpResponse {
    private final int statusCode;
    private final boolean success;
    
    public HttpResponse(int statusCode, boolean success) {
        this.statusCode = statusCode;
        this.success = success;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public boolean isSuccess() {
        return success;
    }
}