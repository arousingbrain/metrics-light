import java.util.*;

public class verify_correlation_id {
    public static void main(String[] args) {
        System.out.println("=== Verification: APPLOADID Format ===");
        System.out.println();
        
        for (int i = 1; i <= 3; i++) {
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            System.out.println("Request " + i + " correlation ID: APPLOADID" + uuid);
        }
        
        System.out.println();
        System.out.println("Format: APPLOADID{6-character-uuid}");
    }
}