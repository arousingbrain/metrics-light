package com.metrics.light;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses curl commands to extract HTTP method, URL, headers, and request body.
 */
public class CurlCommandParser {
    
    // Pattern for extracting URL from curl command (currently using manual parsing)
    private static final Pattern METHOD_PATTERN = Pattern.compile("-X\\s+([A-Z]+)");
    private static final Pattern HEADER_PATTERN = Pattern.compile("-H\\s+['\"]([^'\"]+)['\"]");
    private static final Pattern DATA_PATTERN = Pattern.compile("-d\\s+['\"]([^'\"]+)['\"]");
    
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
        Matcher matcher = DATA_PATTERN.matcher(curlCommand);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}