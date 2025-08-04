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
    echo "  java -jar target/metrics-light-1.0.0.jar -e http://httpbin.org/get -u 10 -t 2 -d 30"
    echo "  java -jar target/metrics-light-1.0.0.jar -e https://jsonplaceholder.typicode.com/posts/1 -u 50 -t 5 -d 60"
    echo ""
    echo "Parameters:"
    echo "  -e, --endpoint: Target endpoint URL"
    echo "  -u, --users: Number of concurrent users"
    echo "  -t, --threads: Number of threads"
    echo "  -d, --duration: Test duration in seconds"
    echo "  -h, --help: Show help"
else
    echo "Build failed!"
    exit 1
fi