import java.util.*;

public class verify_uuid_replacement {
    public static void main(String[] args) {
        System.out.println("=== UUID Token Replacement Verification ===");
        System.out.println();
        
        String curlCommand = "curl -X POST http://example.com/api -H 'Request-ID: {uuid}' -H 'Content-Type: application/json' -d '{\"correlationId\":\"{uuid}\",\"userId\":\"{uuid}\"}'";
        
        System.out.println("Original curl command:");
        System.out.println(curlCommand);
        System.out.println();
        
        System.out.println("Sample replacements (simulating what the app does for each request):");
        System.out.println();
        
        for (int i = 1; i <= 3; i++) {
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            String generatedId = "APPLOADID" + uuid;
            String processedCommand = curlCommand.replace("{uuid}", generatedId);
            
            System.out.println("Request " + i + ":");
            System.out.println("  Generated ID: " + generatedId);
            System.out.println("  Processed command: " + processedCommand);
            System.out.println();
        }
        
        System.out.println("Note: Each {uuid} token is replaced with the SAME generated ID within a single request,");
        System.out.println("but each new request gets a completely fresh ID.");
    }
}