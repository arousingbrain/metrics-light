#!/bin/bash

# Test examples for Metrics Light with curl command support

echo "=== Metrics Light - Curl Command Testing Examples ==="
echo ""

JAR_FILE="target/metrics-light-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found. Please run './build.sh' first."
    exit 1
fi

echo "1. Testing basic GET request..."
java -jar $JAR_FILE -c "curl http://httpbin.org/get" -u 3 -t 1 -d 5

echo ""
echo "2. Testing POST request with headers and body..."
java -jar $JAR_FILE -c "curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\"message\":\"test\"}'" -u 2 -t 1 -d 5

echo ""
echo "3. Testing request with correlation ID (will be auto-generated)..."
java -jar $JAR_FILE -c "curl -X POST http://httpbin.org/post -H 'one-data-correlation-id: original-value' -H 'Content-Type: application/json' -d '{\"correlationTest\":true}'" -u 2 -t 1 -d 5

echo ""
echo "4. Testing with delay between requests..."
java -jar $JAR_FILE -c "curl http://httpbin.org/get" -u 2 -t 1 -d 5 -r 500

echo ""
echo "=== All tests completed! ==="