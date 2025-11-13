# Docker Deployment Guide

This guide explains how to build and deploy the OpenCode Java SDK and its examples using Docker.

## Prerequisites

- Docker 20.10+
- Docker Compose v2.0+
- Maven 3.6+ (for local builds)

## Quick Start

### 1. Build Everything with Docker Compose

```bash
# From the root directory
docker-compose build
docker-compose up
```

This will:
- Build the OpenCode Java SDK
- Build and start the Spring Chat App on port 8080
- Build and start the OpenAI API Bridge on port 8081

### 2. Access the Services

- **Spring Chat App**: http://localhost:8080
- **OpenAI API Bridge**: http://localhost:8081
- **API Documentation**: http://localhost:8081/swagger-ui.html

## Individual Container Builds

### OpenCode Java SDK (Library)

```bash
# Build the SDK container (for testing/validation)
docker build -t opencode-java-sdk:latest .

# Run to verify build
docker run --rm opencode-java-sdk:latest
```

### Spring Chat App

```bash
# Option 1: Build from root with docker-compose
docker-compose build chat-app

# Option 2: Build standalone (requires SDK in local Maven repo)
cd examples/spring-chat-app
mvn install -f ../../pom.xml  # Install SDK first
docker build -f Dockerfile.standalone -t chat-app .
docker run -p 8080:8080 chat-app
```

### OpenAI API Bridge

```bash
# Option 1: Build from root with docker-compose
docker-compose build api-bridge

# Option 2: Build standalone (requires SDK in local Maven repo)
cd examples/openai-api-bridge
mvn install -f ../../pom.xml  # Install SDK first
docker build -f Dockerfile.standalone -t api-bridge .
docker run -p 8081:8081 \
  -e OPENCODE_URL=http://host.docker.internal:8765 \
  api-bridge
```

## Environment Variables

### OpenAI API Bridge

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENCODE_URL` | OpenCode server URL | `http://localhost:8765` |
| `OPENCODE_AUTO_START` | Auto-start OpenCode server | `false` |
| `API_SECURITY_ENABLED` | Enable API key auth | `false` |
| `CORS_ALLOWED_ORIGINS` | CORS origins | `*` |
| `SERVER_PORT` | Server port | `8081` |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |

### Spring Chat App

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `8080` |
| `JAVA_OPTS` | JVM options | `-Xmx256m -Xms128m` |

## Docker Compose Configuration

The `docker-compose.yml` provides:
- Automated SDK building
- Service dependencies
- Network isolation
- Health checks
- Restart policies

### Custom OpenCode Server

To use an OpenCode server container, uncomment the `opencode` service in `docker-compose.yml`:

```yaml
opencode:
  image: opencode/opencode:latest  # Replace with actual image
  container_name: opencode-server
  ports:
    - "8765:8765"
  command: serve --host 0.0.0.0 --port 8765
```

Then update the API Bridge to use the container network:
```yaml
api-bridge:
  environment:
    - OPENCODE_URL=http://opencode:8765  # Use container name
```

## Production Deployment

### Security Considerations

1. **Enable API Security**:
```yaml
environment:
  - API_SECURITY_ENABLED=true
  - API_KEYS=your-secure-api-key
```

2. **Restrict CORS**:
```yaml
environment:
  - CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

3. **Use Secrets Management**:
```yaml
secrets:
  - api_keys
environment:
  - API_KEYS=/run/secrets/api_keys
```

### Resource Limits

Add resource constraints for production:

```yaml
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 256M
```

### Scaling

Scale services horizontally:

```bash
docker-compose up --scale api-bridge=3
```

## Troubleshooting

### Container Fails to Start

1. Check logs:
```bash
docker-compose logs api-bridge
docker-compose logs chat-app
```

2. Verify SDK is built:
```bash
docker run --rm opencode-java-sdk:latest
```

### Connection Issues

1. Check network:
```bash
docker network ls
docker network inspect opencode-spring-java-sdk_opencode-network
```

2. Test connectivity:
```bash
docker-compose exec api-bridge wget -O- http://opencode:8765/health
```

### Build Failures

1. Clean and rebuild:
```bash
docker-compose down
docker system prune -f
docker-compose build --no-cache
```

2. Check Maven dependencies:
```bash
mvn clean install
```

## Docker Hub Deployment

To push images to Docker Hub:

```bash
# Tag images
docker tag opencode-java-sdk:latest yourusername/opencode-java-sdk:latest
docker tag chat-app:latest yourusername/opencode-chat-app:latest
docker tag api-bridge:latest yourusername/openai-api-bridge:latest

# Push to registry
docker push yourusername/opencode-java-sdk:latest
docker push yourusername/opencode-chat-app:latest
docker push yourusername/openai-api-bridge:latest
```

## Kubernetes Deployment

For Kubernetes deployment, use the Docker images with Helm charts or manifests:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: openai-api-bridge
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api-bridge
  template:
    metadata:
      labels:
        app: api-bridge
    spec:
      containers:
      - name: api-bridge
        image: yourusername/openai-api-bridge:latest
        ports:
        - containerPort: 8081
        env:
        - name: OPENCODE_URL
          value: "http://opencode-service:8765"
```

## Support

For issues or questions:
- Check the logs: `docker-compose logs -f`
- Review environment variables
- Ensure all services are healthy: `docker-compose ps`