#!/bin/bash
# Launch script for OpenCode All-in-One Package

set -e

echo "🚀 OpenCode All-in-One Package Launcher"
echo "========================================"

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.9+ with Java 24."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?\K[0-9]+')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Build the application
echo ""
echo "📦 Building application..."
mvn clean package -DskipTests

# Check if build was successful
if [ ! -f target/opencode-all-in-one-*.jar ]; then
    echo "❌ Build failed. JAR file not found."
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""

# Parse command line arguments
AUTO_START=${OPENCODE_AUTO_START:-false}
OPENCODE_URL=${OPENCODE_URL:-http://localhost:8765}
SERVER_PORT=${SERVER_PORT:-8081}

# Display configuration
echo "Configuration:"
echo "  - Server Port: $SERVER_PORT"
echo "  - OpenCode URL: $OPENCODE_URL"
echo "  - Auto Start OpenCode: $AUTO_START"
echo ""

# Run the application
echo "🌟 Starting OpenCode All-in-One Package..."
echo ""
echo "Services available at:"
echo "  - OpenAI API Bridge: http://localhost:$SERVER_PORT"
echo "  - API Documentation: http://localhost:$SERVER_PORT/swagger-ui.html"
echo "  - Health Check: http://localhost:$SERVER_PORT/actuator/health"
echo ""

export OPENCODE_URL=$OPENCODE_URL
export OPENCODE_AUTO_START=$AUTO_START
export SERVER_PORT=$SERVER_PORT

java --enable-preview -jar target/opencode-all-in-one-*.jar
