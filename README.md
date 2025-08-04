# # Metrics Light - Lightweight Java Load Testing Tool

A lightweight Java-based load testing application similar to JMeter that sends HTTP requests to endpoints and tracks performance metrics.

## Features

- **Lightweight**: Standalone JAR file with minimal dependencies
- **Comprehensive Metrics**: Captures TPS, percentiles (P75, P90, P99, P99.9, P99.99), and response times
- **Multi-threaded**: Configurable thread count for concurrent load generation
- **Real-time Reporting**: Progress updates during test execution
- **Simple CLI**: Easy command-line interface with clear parameters

## Metrics Captured

- **Average TPS** (Transactions Per Second)
- **Maximum TPS**
- **Response Time Percentiles**: P75, P90, P99, P99.9, P99.99
- **Success Rate**
- **Min/Max/Average Response Times**
- **Total/Successful/Failed Request Counts**

## Requirements

- Java 11 or higher
- Maven 3.6+ (for building)

## Building

Use the provided build script:

```bash
./build.sh
```

Or build manually with Maven:

```bash
mvn clean package
```

The standalone JAR will be created at `target/metrics-light-1.0.0.jar`

## Usage

```bash
java -jar target/metrics-light-1.0.0.jar -e <endpoint> -u <users> -t <threads> -d <duration> [-r <delay>]
```

### Parameters

- `-e, --endpoint`: Target endpoint URL (required)
- `-u, --users`: Number of concurrent users (required)
- `-t, --threads`: Number of threads (required)
- `-d, --duration`: Test duration in seconds (required)
- `-r, --delay`: Delay between requests in milliseconds (optional, default: 0)
- `-h, --help`: Show help message

### Examples

**Basic load test:**
```bash
java -jar target/metrics-light-1.0.0.jar -e http://httpbin.org/get -u 10 -t 2 -d 30
```

**Heavy load test:**
```bash
java -jar target/metrics-light-1.0.0.jar -e https://jsonplaceholder.typicode.com/posts/1 -u 100 -t 10 -d 60
```

**Load test with delay (100ms between requests):**
```bash
java -jar target/metrics-light-1.0.0.jar -e http://httpbin.org/get -u 20 -t 4 -d 60 -r 100
```

**Local application test:**
```bash
java -jar target/metrics-light-1.0.0.jar -e http://localhost:8080/api/health -u 50 -t 5 -d 120 -r 50
```

## Sample Output

```
Starting load test with configuration:
  Endpoint: http://httpbin.org/get
  Users: 10
  Threads: 2
  Duration: 30 seconds
  Delay: 0 ms

Requests sent: 245
Requests sent: 489
Requests sent: 732

Test duration reached. Stopping...

================================================================================
LOAD TEST COMPLETED
================================================================================
Total Requests: 1156
Successful Requests: 1156
Failed Requests: 0
Success Rate: 100.00%

Average TPS: 38.53
Maximum TPS: 42.00

Response Time P75: 156.23 ms
Response Time P90: 198.45 ms
Response Time P99: 267.89 ms
Response Time P99.9: 312.45 ms
Response Time P99.99: 325.67 ms

Min Response Time: 89.34 ms
Max Response Time: 325.67 ms
Average Response Time: 142.78 ms
```

## Architecture

The application consists of several key components:

- **LoadTestApp**: Main entry point with CLI argument parsing
- **LoadTestExecutor**: Orchestrates the load test execution
- **MetricsCollector**: Collects and calculates performance statistics
- **HttpRequestSender**: Handles HTTP requests with connection pooling
- **TestConfiguration**: Configuration data holder

## Configuration Guidelines

- **Users vs Threads**: Number of threads should not exceed number of users
- **Thread Count**: Start with CPU cores × 2, adjust based on target system
- **Duration**: Allow sufficient time for meaningful statistics (minimum 30 seconds recommended)
- **Delay**: Use to control request rate and avoid overwhelming the target system
  - `0 ms` (default): Maximum possible request rate
  - `100-500 ms`: Moderate load testing
  - `1000+ ms`: Slow, sustained load testing
- **Endpoint**: Ensure the target endpoint can handle the expected load

## Limitations

- Currently supports only GET requests
- No authentication mechanisms built-in
- No custom headers or request body support
- Designed for HTTP/HTTPS endpoints only

## License

This project is licensed under the terms specified in the LICENSE file.