# Multi-stage build for OpenCode All-in-One Package
FROM maven:3.9-eclipse-temurin-24-jammy AS builder

WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:24-jre-jammy

WORKDIR /app

# Install OpenCode binary (optional - download latest release)
# Uncomment if you want to bundle OpenCode binary
# RUN apt-get update && apt-get install -y curl && \
#     curl -L https://github.com/opencode/opencode/releases/latest/download/opencode-linux-x64 -o /usr/local/bin/opencode && \
#     chmod +x /usr/local/bin/opencode

# Copy the built jar
COPY --from=builder /app/target/opencode-all-in-one-*.jar app.jar

# Expose ports
# 8081 - OpenAI Bridge API
# 8765 - OpenCode Server (if running)
EXPOSE 8081 8765

# Environment variables
ENV OPENCODE_URL=http://localhost:8765
ENV OPENCODE_AUTO_START=false
ENV API_SECURITY_ENABLED=false
ENV CORS_ALLOWED_ORIGINS=*
ENV SERVER_PORT=8081

# Run the application
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
