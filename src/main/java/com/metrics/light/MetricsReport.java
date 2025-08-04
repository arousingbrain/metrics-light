package com.metrics.light;

/**
 * Contains the results of a load test metrics analysis.
 */
public class MetricsReport {
    private final long totalRequests;
    private final long successfulRequests;
    private final long failedRequests;
    private final double successRate;
    private final double averageTps;
    private final double maxTps;
    private final double p75;
    private final double p90;
    private final double p99;
    private final double p99_9;
    private final double p99_99;
    private final double minResponseTime;
    private final double maxResponseTime;
    private final double averageResponseTime;
    
    public MetricsReport(long totalRequests, long successfulRequests, long failedRequests,
                        double successRate, double averageTps, double maxTps,
                        double p75, double p90, double p99, double p99_9, double p99_99,
                        double minResponseTime, double maxResponseTime, double averageResponseTime) {
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.successRate = successRate;
        this.averageTps = averageTps;
        this.maxTps = maxTps;
        this.p75 = p75;
        this.p90 = p90;
        this.p99 = p99;
        this.p99_9 = p99_9;
        this.p99_99 = p99_99;
        this.minResponseTime = minResponseTime;
        this.maxResponseTime = maxResponseTime;
        this.averageResponseTime = averageResponseTime;
    }
    
    // Getters
    public long getTotalRequests() { return totalRequests; }
    public long getSuccessfulRequests() { return successfulRequests; }
    public long getFailedRequests() { return failedRequests; }
    public double getSuccessRate() { return successRate; }
    public double getAverageTps() { return averageTps; }
    public double getMaxTps() { return maxTps; }
    public double getP75() { return p75; }
    public double getP90() { return p90; }
    public double getP99() { return p99; }
    public double getP99_9() { return p99_9; }
    public double getP99_99() { return p99_99; }
    public double getMinResponseTime() { return minResponseTime; }
    public double getMaxResponseTime() { return maxResponseTime; }
    public double getAverageResponseTime() { return averageResponseTime; }
}