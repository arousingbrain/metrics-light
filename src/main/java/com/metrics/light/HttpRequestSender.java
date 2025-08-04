package com.metrics.light;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import java.util.Map;

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
    
    public HttpResponse sendRequest(RequestDetails requestDetails) throws Exception {
        ClassicHttpRequest request = createHttpRequest(requestDetails);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            boolean success = statusCode >= 200 && statusCode < 300;
            
            return new HttpResponse(statusCode, success);
            
        } catch (Exception e) {
            // Log error but don't print to avoid spam during load test
            return new HttpResponse(0, false);
        }
    }
    
    private ClassicHttpRequest createHttpRequest(RequestDetails requestDetails) throws Exception {
        String method = requestDetails.getMethod().toUpperCase();
        String url = requestDetails.getUrl();
        
        ClassicHttpRequest request;
        
        switch (method) {
            case "GET":
                request = new HttpGet(url);
                break;
            case "POST":
                request = new HttpPost(url);
                break;
            case "PUT":
                request = new HttpPut(url);
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            case "PATCH":
                request = new HttpPatch(url);
                break;
            case "HEAD":
                request = new HttpHead(url);
                break;
            case "OPTIONS":
                request = new HttpOptions(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        
        // Add headers
        Map<String, String> headers = requestDetails.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        
        // Add body for methods that support it
        if (requestDetails.getBody() != null && (request instanceof HttpPost || 
                                               request instanceof HttpPut || 
                                               request instanceof HttpPatch)) {
            StringEntity entity = new StringEntity(requestDetails.getBody());
            ((HttpUriRequestBase) request).setEntity(entity);
        }
        
        return request;
    }
    
    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            // Ignore close errors
        }
    }
}