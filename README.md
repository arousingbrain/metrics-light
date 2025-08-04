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

First, create a `curl.txt` file in your current directory containing the curl command you want to test:

```bash
echo "curl -X POST http://api.example.com/data -H 'Content-Type: application/json' -d '{\"key\":\"value\"}'" > curl.txt
```

Then run the load test:

```bash
java -jar target/metrics-light-1.0.0.jar -u <users> -t <threads> -d <duration> [-r <delay>]
```

### Parameters

- `-u, --users`: Number of concurrent users (required)
- `-t, --threads`: Number of threads (required)
- `-d, --duration`: Test duration in seconds (required)
- `-r, --delay`: Delay between requests in milliseconds (optional, default: 0)
- `-h, --help`: Show help message

### Curl Command File

The application reads the curl command from a file named `curl.txt` in the current directory. This file should contain a single curl command that will be executed for each request.

**File Requirements:**
- Must be named `curl.txt`
- Must be in the current working directory
- Should contain a valid curl command
- Can span multiple lines using backslashes (`\`)
- Supports `{uuid}` token replacement anywhere in the command

**Example Files:**
Check the `examples/` directory for sample curl.txt files:
- `simple-get.curl.txt` - Basic GET request
- `post-with-auth.curl.txt` - POST with authentication
- `complex-api.curl.txt` - Complex multi-line request with JSON

### Examples

**Basic GET request:**
```bash
# Create curl.txt file
echo "curl http://httpbin.org/get" > curl.txt

# Run load test
java -jar target/metrics-light-1.0.0.jar -u 10 -t 2 -d 30
```

**POST request with headers and body:**
```bash
# Create curl.txt file
echo "curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\"key\":\"value\"}'" > curl.txt

# Run load test
java -jar target/metrics-light-1.0.0.jar -u 50 -t 5 -d 60
```

**Request with UUID token (will be auto-generated for each request):**
```bash
# Create curl.txt file
cat > curl.txt << 'EOF'
curl -X POST http://api.example.com/data \
  -H 'Correlation-ID: {uuid}' \
  -H 'Content-Type: application/json' \
  -d '{"requestId":"{uuid}","data":"test"}'
EOF

# Run load test
java -jar target/metrics-light-1.0.0.jar -u 20 -t 4 -d 60 -r 100
```

**Complex API test with authentication:**
```bash
# Create curl.txt file
cat > curl.txt << 'EOF'
curl -X PUT http://localhost:8080/api/users/123 \
  -H 'Authorization: Bearer token123' \
  -H 'Content-Type: application/json' \
  -H 'Request-ID: {uuid}' \
  -d '{"name":"John","sessionId":"{uuid}"}'
EOF

# Run load test
java -jar target/metrics-light-1.0.0.jar -u 25 -t 5 -d 120 -r 50
```

## Sample Output

```
Starting load test with configuration:
  Curl Command (from curl.txt): curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{"key":"value"}'
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
- **UUID Token Replacement**: Automatically replaces `{uuid}` tokens anywhere in the curl command with `APPLOADID{uuid}` where `{uuid}` is the first 6 characters of a generated UUID for each request

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