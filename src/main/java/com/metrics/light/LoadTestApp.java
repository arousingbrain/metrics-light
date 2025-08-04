package com.metrics.light;

import org.apache.commons.cli.*;

/**
 * Main entry point for the Metrics Light load testing application.
 * 
 * Usage: java -jar metrics-light.jar -e <endpoint> -u <users> -t <threads> -d <duration>
 */
public class LoadTestApp {
    
    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            // Check for help first, before parsing required options
            if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
                printUsage(options);
                return;
            }
            
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) {
                printUsage(options);
                return;
            }
            
            TestConfiguration config = parseConfiguration(cmd);
            validateConfiguration(config);
            
            System.out.println("Starting load test with configuration:");
            System.out.println("  Endpoint: " + config.getEndpoint());
            System.out.println("  Users: " + config.getUsers());
            System.out.println("  Threads: " + config.getThreads());
            System.out.println("  Duration: " + config.getDurationSeconds() + " seconds");
            System.out.println("  Delay: " + config.getDelayMs() + " ms");
            System.out.println();
            
            LoadTestExecutor executor = new LoadTestExecutor(config);
            executor.execute();
            
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            printUsage(options);
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error executing load test: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption(Option.builder("e")
                .longOpt("endpoint")
                .hasArg()
                .required()
                .desc("Target endpoint URL")
                .build());
                
        options.addOption(Option.builder("u")
                .longOpt("users")
                .hasArg()
                .required()
                .desc("Number of concurrent users")
                .build());
                
        options.addOption(Option.builder("t")
                .longOpt("threads")
                .hasArg()
                .required()
                .desc("Number of threads")
                .build());
                
        options.addOption(Option.builder("d")
                .longOpt("duration")
                .hasArg()
                .required()
                .desc("Test duration in seconds")
                .build());
                
        options.addOption(Option.builder("r")
                .longOpt("delay")
                .hasArg()
                .desc("Delay between requests in milliseconds (default: 0)")
                .build());
                
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show help")
                .build());
                
        return options;
    }
    
    private static TestConfiguration parseConfiguration(CommandLine cmd) {
        String endpoint = cmd.getOptionValue("e");
        int users = Integer.parseInt(cmd.getOptionValue("u"));
        int threads = Integer.parseInt(cmd.getOptionValue("t"));
        int duration = Integer.parseInt(cmd.getOptionValue("d"));
        int delay = cmd.hasOption("r") ? Integer.parseInt(cmd.getOptionValue("r")) : 0;
        
        return new TestConfiguration(endpoint, users, threads, duration, delay);
    }
    
    private static void validateConfiguration(TestConfiguration config) {
        if (config.getUsers() <= 0) {
            throw new IllegalArgumentException("Number of users must be positive");
        }
        if (config.getThreads() <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive");
        }
        if (config.getDurationSeconds() <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (config.getDelayMs() < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        if (config.getThreads() > config.getUsers()) {
            throw new IllegalArgumentException("Number of threads cannot exceed number of users");
        }
    }
    
    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar metrics-light.jar", 
                "Lightweight load testing application\n\n", 
                options, 
                "\nExample: java -jar metrics-light.jar -e http://localhost:8080/api/test -u 100 -t 10 -d 60 -r 100");
    }
}