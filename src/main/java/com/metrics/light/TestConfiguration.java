package com.metrics.light;

/**
 * Configuration holder for load test parameters.
 */
public class TestConfiguration {
    private final String endpoint;
    private final int users;
    private final int threads;
    private final int durationSeconds;
    private final int delayMs;
    
    public TestConfiguration(String endpoint, int users, int threads, int durationSeconds, int delayMs) {
        this.endpoint = endpoint;
        this.users = users;
        this.threads = threads;
        this.durationSeconds = durationSeconds;
        this.delayMs = delayMs;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public int getUsers() {
        return users;
    }
    
    public int getThreads() {
        return threads;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public long getDurationMillis() {
        return durationSeconds * 1000L;
    }
    
    public int getDelayMs() {
        return delayMs;
    }
}