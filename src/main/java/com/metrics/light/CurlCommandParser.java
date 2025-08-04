package com.metrics.light;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses curl commands to extract HTTP method, URL, headers, and request body.
 * Supports {uuid} token replacement with generated correlation IDs.
 */
public class CurlCommandParser {
    
    // Pattern for extracting URL from curl command (currently using manual parsing)
    private static final Pattern METHOD_PATTERN = Pattern.compile("-X\\s+([A-Z]+)");
    private static final Pattern HEADER_PATTERN = Pattern.compile("-H\\s+['\"]([^'\"]+)['\"]");
    private static final Pattern DATA_PATTERN = Pattern.compile("(?:-d|--data|--data-raw)\\s+(['\"])([^'\"]*(?:\\\\.[^'\"]*)*?)\\1");
    
    public static RequestDetails parse(String curlCommand) {
        if (curlCommand == null || curlCommand.trim().isEmpty()) {
            throw new IllegalArgumentException("Curl command cannot be null or empty");
        }
        
        String url = extractUrl(curlCommand);
        String method = extractMethod(curlCommand);
        Map<String, String> headers = extractHeaders(curlCommand);
        String body = extractBody(curlCommand);
        
        return new RequestDetails(url, method, headers, body);
    }
    
    /**
     * Creates a RequestDetails with {uuid} tokens replaced with generated correlation IDs.
     */
    public static RequestDetails parseWithUuidReplacement(String curlCommand) {
        if (curlCommand == null || curlCommand.trim().isEmpty()) {
            throw new IllegalArgumentException("Curl command cannot be null or empty");
        }
        
        // Generate unique UUID for this request
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String generatedId = "APPLOADID" + uuid;
        
        // Replace all {uuid} tokens in the curl command
        String processedCommand = curlCommand.replace("{uuid}", generatedId);
        
        // Parse the processed command
        String url = extractUrl(processedCommand);
        String method = extractMethod(processedCommand);
        Map<String, String> headers = extractHeaders(processedCommand);
        String body = extractBody(processedCommand);
        
        return new RequestDetails(url, method, headers, body);
    }
    
    private static String extractUrl(String curlCommand) {
        // Remove 'curl' and look for the URL
        // URLs can be quoted or unquoted
        String[] parts = curlCommand.split("\\s+");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Skip curl command and flags
            if (part.equals("curl") || part.startsWith("-")) {
                // Skip flags that have values
                if (part.equals("-X") || part.equals("-H") || part.equals("-d") || 
                    part.equals("--request") || part.equals("--header") || part.equals("--data")) {
                    i++; // Skip the next argument as well
                }
                continue;
            }
            
            // Remove quotes if present
            String url = part.replaceAll("^['\"]|['\"]$", "");
            
            // Basic URL validation
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }
        }
        
        throw new IllegalArgumentException("No valid URL found in curl command");
    }
    
    private static String extractMethod(String curlCommand) {
        Matcher matcher = METHOD_PATTERN.matcher(curlCommand);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "GET"; // Default method
    }
    
    private static Map<String, String> extractHeaders(String curlCommand) {
        Map<String, String> headers = new HashMap<>();
        Matcher matcher = HEADER_PATTERN.matcher(curlCommand);
        
        while (matcher.find()) {
            String headerLine = matcher.group(1);
            String[] headerParts = headerLine.split(":", 2);
            if (headerParts.length == 2) {
                String key = headerParts[0].trim();
                String value = headerParts[1].trim();
                headers.put(key, value);
            }
        }
        
        return headers;
    }
    
    private static String extractBody(String curlCommand) {
        // Try multiple patterns for different curl data flag formats
        String[] patterns = {
            "(?:-d|--data|--data-raw)\\s+'([^']*)'",      // Single quotes
            "(?:-d|--data|--data-raw)\\s+\"([^\"]*)\""     // Double quotes
        };
        
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(curlCommand);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        return null;
    }
}