# Multi-stage build for OpenAI API Bridge
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# First build the SDK (as dependency)
COPY ../../pom.xml /sdk/pom.xml
COPY ../../src /sdk/src
RUN cd /sdk && mvn clean install -DskipTests

# Copy and build the API bridge
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the jar file
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

# Environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    OPENCODE_URL="http://localhost:8765" \
    OPENCODE_AUTO_START="false" \
    API_SECURITY_ENABLED="false" \
    CORS_ALLOWED_ORIGINS="*" \
    SERVER_PORT="8081"

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]