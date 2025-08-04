#!/bin/bash

# Build script for Metrics Light load testing application

echo "Building Metrics Light..."

# Clean and compile
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo ""
    echo "Usage examples:"
    echo "  java -jar target/metrics-light-1.0.0.jar -c \"curl http://httpbin.org/get\" -u 10 -t 2 -d 30"
    echo "  java -jar target/metrics-light-1.0.0.jar -c \"curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\\\"key\\\":\\\"value\\\"}'\" -u 50 -t 5 -d 60"
    echo "  java -jar target/metrics-light-1.0.0.jar -c \"curl http://httpbin.org/get\" -u 20 -t 4 -d 60 -r 100"
    echo ""
    echo "Parameters:"
    echo "  -c, --curl: Curl command to execute"
    echo "  -u, --users: Number of concurrent users"
    echo "  -t, --threads: Number of threads"
    echo "  -d, --duration: Test duration in seconds"
    echo "  -r, --delay: Delay between requests in milliseconds (optional, default: 0)"
    echo "  -h, --help: Show help"
else
    echo "Build failed!"
    exit 1
fi