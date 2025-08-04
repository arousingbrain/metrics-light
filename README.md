# # Metrics Light - Lightweight Java Load Testing Tool

A lightweight Java-based load testing application similar to JMeter that sends HTTP requests to endpoints and tracks performance metrics.

## Features

- **Lightweight**: Standalone JAR file with minimal dependencies
- **Comprehensive Metrics**: Captures TPS, percentiles (P75, P90, P99, P99.9, P99.99), and response times
- **Multi-threaded**: Configurable thread count for concurrent load generation
- **Real-time Reporting**: Progress updates during test execution
- **Curl Command Support**: Parse and execute curl commands with headers, body, and different HTTP methods
- **Auto-Generated Correlation IDs**: Automatically generates unique correlation IDs for each request
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
java -jar target/metrics-light-1.0.0.jar -c "curl command" -u <users> -t <threads> -d <duration> [-r <delay>]
```

### Parameters

- `-c, --curl`: Curl command to execute (required)
- `-u, --users`: Number of concurrent users (required)
- `-t, --threads`: Number of threads (required)
- `-d, --duration`: Test duration in seconds (required)
- `-r, --delay`: Delay between requests in milliseconds (optional, default: 0)
- `-h, --help`: Show help message

### Examples

**Basic GET request:**
```bash
java -jar target/metrics-light-1.0.0.jar -c "curl http://httpbin.org/get" -u 10 -t 2 -d 30
```

**POST request with headers and body:**
```bash
java -jar target/metrics-light-1.0.0.jar -c "curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\"key\":\"value\"}'" -u 50 -t 5 -d 60
```

**Request with correlation ID (will be auto-generated for each request):**
```bash
java -jar target/metrics-light-1.0.0.jar -c "curl -X POST http://api.example.com/data -H 'one-data-correlation-id: original-value' -H 'Content-Type: application/json' -d '{\"data\":\"test\"}'" -u 20 -t 4 -d 60 -r 100
```

**Complex API test with authentication:**
```bash
java -jar target/metrics-light-1.0.0.jar -c "curl -X PUT http://localhost:8080/api/users/123 -H 'Authorization: Bearer token123' -H 'Content-Type: application/json' -d '{\"name\":\"John\"}'" -u 25 -t 5 -d 120 -r 50
```

## Sample Output

```
Starting load test with configuration:
  Curl Command: curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{"key":"value"}'
  Users: 10
  Threads: 2
  Duration: 30 seconds
  Delay: 0 ms

Parsed request details:
  URL: http://httpbin.org/post
  Method: POST
  Headers: 1 header(s)
  Has Body: true

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
- **CurlCommandParser**: Parses curl commands to extract request details
- **RequestDetails**: Holds parsed request information with correlation ID generation
- **TestConfiguration**: Configuration data holder

## Curl Command Support

The application parses curl commands to extract:
- **HTTP Method**: GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
- **Headers**: All `-H` or `--header` flags
- **Request Body**: Data from `-d` or `--data` flags
- **Special Correlation ID**: Automatically replaces `one-data-correlation-id` header values with `CSSLOADTEST{uuid}` where `{uuid}` is the first 6 characters of a generated UUID for each request

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

- Curl command parsing supports basic syntax (single quotes, double quotes, basic flags)
- Complex curl features (file uploads, advanced authentication, cookies) not fully supported
- Designed for HTTP/HTTPS endpoints only
- No support for curl config files or environment variables

## License

This project is licensed under the terms specified in the LICENSE file.