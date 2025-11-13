# Multi-stage build for Spring Chat App
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# First build the SDK (as dependency)
COPY ../../pom.xml /sdk/pom.xml
COPY ../../src /sdk/src
RUN cd /sdk && mvn clean install -DskipTests

# Copy and build the chat app
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
ENV JAVA_OPTS="-Xmx256m -Xms128m" \
    SERVER_PORT="8080"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]