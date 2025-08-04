#!/bin/bash

# Test examples for Metrics Light with curl.txt file support

echo "=== Metrics Light - File-based Curl Command Testing Examples ==="
echo ""

JAR_FILE="target/metrics-light-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found. Please run './build.sh' first."
    exit 1
fi

echo "1. Testing basic GET request..."
echo "curl http://httpbin.org/get" > curl.txt
java -jar $JAR_FILE -u 3 -t 1 -d 5

echo ""
echo "2. Testing POST request with headers and body..."
echo "curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\"message\":\"test\"}'" > curl.txt
java -jar $JAR_FILE -u 2 -t 1 -d 5

echo ""
echo "3. Testing request with UUID token replacement (will be auto-generated)..."
cat > curl.txt << 'EOF'
curl -X POST http://httpbin.org/post -H 'Request-ID: {uuid}' -H 'Content-Type: application/json' -d '{"correlationId":"{uuid}","test":true}'
EOF
java -jar $JAR_FILE -u 2 -t 1 -d 5

echo ""
echo "4. Testing with delay between requests..."
echo "curl http://httpbin.org/get" > curl.txt
java -jar $JAR_FILE -u 2 -t 1 -d 5 -r 500

# Clean up
rm -f curl.txt

echo ""
echo "=== All tests completed! ==="