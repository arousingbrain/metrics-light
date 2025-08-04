package com.metrics.light;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

/**
 * Collects and calculates performance metrics for the load test.
 */
public class MetricsCollector {
    private final ConcurrentLinkedQueue<Double> responseTimes;
    private final AtomicLong totalRequests;
    private final AtomicLong successfulRequests;
    private final AtomicLong failedRequests;
    private final AtomicReference<Double> minResponseTime;
    private final AtomicReference<Double> maxResponseTime;
    private final Map<Long, AtomicLong> requestsPerSecond;
    
    public MetricsCollector() {
        this.responseTimes = new ConcurrentLinkedQueue<>();
        this.totalRequests = new AtomicLong(0);
        this.successfulRequests = new AtomicLong(0);
        this.failedRequests = new AtomicLong(0);
        this.minResponseTime = new AtomicReference<>(Double.MAX_VALUE);
        this.maxResponseTime = new AtomicReference<>(0.0);
        this.requestsPerSecond = new ConcurrentHashMap<>();
    }
    
    public void recordResponse(double responseTime, boolean success) {
        responseTimes.offer(responseTime);
        totalRequests.incrementAndGet();
        
        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        
        // Update min/max response times
        minResponseTime.updateAndGet(current -> Math.min(current, responseTime));
        maxResponseTime.updateAndGet(current -> Math.max(current, responseTime));
        
        // Track requests per second
        long currentSecond = System.currentTimeMillis() / 1000;
        requestsPerSecond.computeIfAbsent(currentSecond, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    public MetricsReport generateReport(long actualDurationMs) {
        List<Double> sortedTimes = new ArrayList<>(responseTimes);
        Collections.sort(sortedTimes);
        
        double averageResponseTime = sortedTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        double p75 = calculatePercentile(sortedTimes, 75.0);
        double p90 = calculatePercentile(sortedTimes, 90.0);
        double p99 = calculatePercentile(sortedTimes, 99.0);
        double p99_9 = calculatePercentile(sortedTimes, 99.9);
        double p99_99 = calculatePercentile(sortedTimes, 99.99);
        
        double actualDurationSeconds = actualDurationMs / 1000.0;
        double averageTps = totalRequests.get() / actualDurationSeconds;
        
        double maxTps = requestsPerSecond.values().stream()
                .mapToLong(AtomicLong::get)
                .max()
                .orElse(0L);
        
        double successRate = totalRequests.get() > 0 ? 
                (successfulRequests.get() * 100.0) / totalRequests.get() : 0.0;
        
        return new MetricsReport(
                totalRequests.get(),
                successfulRequests.get(),
                failedRequests.get(),
                successRate,
                averageTps,
                maxTps,
                p75, p90, p99, p99_9, p99_99,
                minResponseTime.get(),
                maxResponseTime.get(),
                averageResponseTime
        );
    }
    
    private double calculatePercentile(List<Double> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }
        
        if (sortedValues.size() == 1) {
            return sortedValues.get(0);
        }
        
        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        
        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }
        
        double lowerValue = sortedValues.get(lowerIndex);
        double upperValue = sortedValues.get(upperIndex);
        double weight = index - lowerIndex;
        
        return lowerValue + weight * (upperValue - lowerValue);
    }
}