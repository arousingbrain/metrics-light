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
    echo "  echo \"curl http://httpbin.org/get\" > curl.txt"
    echo "  java -jar target/metrics-light-1.0.0.jar -u 10 -t 2 -d 30"
    echo ""
    echo "  echo \"curl -X POST http://httpbin.org/post -H 'Content-Type: application/json' -d '{\\\"key\\\":\\\"value\\\"}'\") > curl.txt"
    echo "  java -jar target/metrics-light-1.0.0.jar -u 50 -t 5 -d 60 -r 100"
    echo ""
    echo "  # For HTTPS using macOS certificate store (recommended):"
    echo "  echo \"curl https://api.github.com\" > curl.txt"
    echo "  java -Djavax.net.ssl.trustStoreType=KeychainStore -jar target/metrics-light-1.0.0.jar -u 10 -t 2 -d 30"
    echo ""
    echo "  # For HTTPS with untrusted certificates (equivalent to curl -k):"
    echo "  echo \"curl https://self-signed.example.com/api\" > curl.txt"
    echo "  java -Dcom.sun.net.ssl.checkRevocation=false -Dtrust_all_cert=true -Djdk.tls.disabledAlgorithms=\\\"\\\" -jar target/metrics-light-1.0.0.jar -u 10 -t 2 -d 30"
    echo ""
    echo "Parameters:"
    echo "  -u, --users: Number of concurrent users"
    echo "  -t, --threads: Number of threads"
    echo "  -d, --duration: Test duration in seconds"
    echo "  -r, --delay: Delay between requests in milliseconds (optional, default: 0)"
    echo "  -h, --help: Show help"
    echo ""
    echo "Note: Place your curl command in a file named 'curl.txt' in the current directory"
    echo "      For SSL certificate issues, the application will provide specific guidance."
else
    echo "Build failed!"
    exit 1
fi