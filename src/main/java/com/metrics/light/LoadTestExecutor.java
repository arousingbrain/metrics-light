package com.metrics.light;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Executes the load test by managing multiple threads and collecting metrics.
 */
public class LoadTestExecutor {
    private final TestConfiguration config;
    private final MetricsCollector metricsCollector;
    private final HttpRequestSender httpSender;
    private final AtomicBoolean shouldStop;
    private final AtomicLong requestCounter;
    private final RequestDetails requestDetails;
    
    public LoadTestExecutor(TestConfiguration config) {
        this.config = config;
        this.metricsCollector = new MetricsCollector();
        this.httpSender = new HttpRequestSender();
        this.shouldStop = new AtomicBoolean(false);
        this.requestCounter = new AtomicLong(0);
        
        // Parse curl command once at startup (without UUID replacement for display)
        this.requestDetails = CurlCommandParser.parse(config.getCurlCommand());
        
        System.out.println("Parsed request details:");
        System.out.println("  URL: " + requestDetails.getUrl());
        System.out.println("  Method: " + requestDetails.getMethod());
        System.out.println("  Headers: " + requestDetails.getHeaders().size() + " header(s)");
        System.out.println("  Has Body: " + (requestDetails.getBody() != null));
        System.out.println();
    }
    
    public void execute() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(config.getThreads());
        
        // Start metrics reporting thread
        ScheduledExecutorService metricsReporter = Executors.newSingleThreadScheduledExecutor();
        metricsReporter.scheduleAtFixedRate(this::reportInterimMetrics, 5, 5, TimeUnit.SECONDS);
        
        // Schedule test termination
        ScheduledExecutorService terminator = Executors.newSingleThreadScheduledExecutor();
        terminator.schedule(() -> {
            shouldStop.set(true);
            System.out.println("\nTest duration reached. Stopping...");
        }, config.getDurationSeconds(), TimeUnit.SECONDS);
        
        long startTime = System.currentTimeMillis();
        
        // Submit worker tasks
        int usersPerThread = config.getUsers() / config.getThreads();
        int remainingUsers = config.getUsers() % config.getThreads();
        
        for (int i = 0; i < config.getThreads(); i++) {
            int threadUsers = usersPerThread + (i < remainingUsers ? 1 : 0);
            executor.submit(new LoadTestWorker(threadUsers));
        }
        
        // Wait for termination
        executor.shutdown();
        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            // Continue waiting
        }
        
        metricsReporter.shutdown();
        terminator.shutdown();
        
        long endTime = System.currentTimeMillis();
        long actualDuration = endTime - startTime;
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("LOAD TEST COMPLETED");
        System.out.println("=".repeat(80));
        
        MetricsReport report = metricsCollector.generateReport(actualDuration);
        printFinalReport(report);
        
        httpSender.close();
    }
    
    private void reportInterimMetrics() {
        if (!shouldStop.get()) {
            long currentRequests = requestCounter.get();
            System.out.printf("Requests sent: %d%n", currentRequests);
        }
    }
    
    private void printFinalReport(MetricsReport report) {
        System.out.printf("Total Requests: %d%n", report.getTotalRequests());
        System.out.printf("Successful Requests: %d%n", report.getSuccessfulRequests());
        System.out.printf("Failed Requests: %d%n", report.getFailedRequests());
        System.out.printf("Success Rate: %.2f%%%n", report.getSuccessRate());
        System.out.println();
        
        System.out.printf("Average TPS: %.2f%n", report.getAverageTps());
        System.out.printf("Maximum TPS: %.2f%n", report.getMaxTps());
        System.out.println();
        
        System.out.printf("Response Time P75: %.2f ms%n", report.getP75());
        System.out.printf("Response Time P90: %.2f ms%n", report.getP90());
        System.out.printf("Response Time P99: %.2f ms%n", report.getP99());
        System.out.printf("Response Time P99.9: %.2f ms%n", report.getP99_9());
        System.out.printf("Response Time P99.99: %.2f ms%n", report.getP99_99());
        System.out.println();
        
        System.out.printf("Min Response Time: %.2f ms%n", report.getMinResponseTime());
        System.out.printf("Max Response Time: %.2f ms%n", report.getMaxResponseTime());
        System.out.printf("Average Response Time: %.2f ms%n", report.getAverageResponseTime());
    }
    
    private class LoadTestWorker implements Runnable {
        private final int users;
        
        public LoadTestWorker(int users) {
            this.users = users;
        }
        
        @Override
        public void run() {
            while (!shouldStop.get()) {
                for (int i = 0; i < users && !shouldStop.get(); i++) {
                    long startTime = System.nanoTime();
                    
                    try {
                        // Generate fresh request with UUID replacement for each request
                        RequestDetails freshRequest = CurlCommandParser.parseWithUuidReplacement(config.getCurlCommand());
                        HttpResponse response = httpSender.sendRequest(freshRequest);
                        long endTime = System.nanoTime();
                        double responseTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
                        
                        metricsCollector.recordResponse(responseTime, response.isSuccess());
                        requestCounter.incrementAndGet();
                        
                    } catch (Exception e) {
                        long endTime = System.nanoTime();
                        double responseTime = (endTime - startTime) / 1_000_000.0;
                        
                        metricsCollector.recordResponse(responseTime, false);
                        requestCounter.incrementAndGet();
                    }
                    
                    // Apply configured delay between requests
                    if (config.getDelayMs() > 0) {
                        try {
                            Thread.sleep(config.getDelayMs());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                
                // Small delay to prevent overwhelming the system if no delay configured
                if (config.getDelayMs() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}