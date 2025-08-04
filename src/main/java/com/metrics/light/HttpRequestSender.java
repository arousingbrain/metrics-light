package com.metrics.light;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;

/**
 * Handles HTTP request sending with connection pooling and timeout configuration.
 */
public class HttpRequestSender {
    private final CloseableHttpClient httpClient;
    
    public HttpRequestSender() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .build();
        
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
    
    public HttpResponse sendRequest(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            boolean success = statusCode >= 200 && statusCode < 300;
            
            return new HttpResponse(statusCode, success);
            
        } catch (Exception e) {
            // Log error but don't print to avoid spam during load test
            return new HttpResponse(0, false);
        }
    }
    
    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            // Ignore close errors
        }
    }
}