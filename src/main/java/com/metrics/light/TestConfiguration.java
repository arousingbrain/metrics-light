package com.metrics.light;

/**
 * Configuration holder for load test parameters.
 */
public class TestConfiguration {
    private final String curlCommand;
    private final int users;
    private final int threads;
    private final int durationSeconds;
    private final int delayMs;
    
    public TestConfiguration(String curlCommand, int users, int threads, int durationSeconds, int delayMs) {
        this.curlCommand = curlCommand;
        this.users = users;
        this.threads = threads;
        this.durationSeconds = durationSeconds;
        this.delayMs = delayMs;
    }
    
    public String getCurlCommand() {
        return curlCommand;
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