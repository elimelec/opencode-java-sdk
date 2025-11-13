# Multi-stage build for OpenCode Java SDK
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage - for testing the SDK
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/target/opencode-java-sdk-*.jar opencode-java-sdk.jar

# Create a simple test script
RUN echo '#!/bin/sh' > /app/test.sh && \
    echo 'echo "OpenCode Java SDK built successfully!"' >> /app/test.sh && \
    echo 'java -jar opencode-java-sdk.jar --version 2>/dev/null || echo "SDK is a library, use with your Java applications"' >> /app/test.sh && \
    chmod +x /app/test.sh

# This is a library, so we just validate it exists
CMD ["/app/test.sh"]