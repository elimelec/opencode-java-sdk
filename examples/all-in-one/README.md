# OpenCode All-in-One Package

A complete, self-contained package that bundles:
- **OpenCode Java SDK** - Full Java client library for OpenCode API
- **OpenAI API Bridge** - OpenAI-compatible REST API for OpenCode
- **OpenCode Launcher Support** - Configuration for running OpenCode server

This is a copy-paste ready package that you can import directly into your project.

## 📦 What's Included

```
all-in-one/
├── src/main/java/dev/sst/opencode/
│   ├── client/          # OpenCode client implementation
│   ├── services/        # Core services (Agents, Completions, Sessions)
│   ├── spring/          # Spring Boot auto-configuration
│   ├── models/          # Request/Response models
│   ├── config/          # Configuration classes
│   └── bridge/          # OpenAI API Bridge application
├── src/main/resources/
│   ├── application.yml  # Default configuration
│   └── META-INF/        # Spring auto-configuration
├── pom.xml              # Maven dependencies (all-in-one)
├── Dockerfile           # Docker build configuration
├── docker-compose.yml   # Docker orchestration
└── run.sh              # Quick launch script
```

## 🚀 Quick Start

### Option 1: Direct Maven Build

```bash
# Build and run
./run.sh

# Or manually:
mvn clean package
java --enable-preview -jar target/opencode-all-in-one-*.jar
```

### Option 2: Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

### Option 3: Import into Your Project

Copy the entire `all-in-one` directory into your project and:

```bash
# Install to local Maven repository
mvn clean install

# Then add to your project's pom.xml:
```

```xml
<dependency>
    <groupId>dev.sst</groupId>
    <artifactId>opencode-all-in-one</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 📚 Usage

### As an OpenAI API Bridge

Once running, the bridge provides OpenAI-compatible endpoints:

```bash
# Chat completions (streaming)
curl -X POST http://localhost:8081/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "claude-3-5-sonnet-20241022",
    "messages": [{"role": "user", "content": "Hello!"}],
    "stream": true
  }'

# List models
curl http://localhost:8081/v1/models

# Health check
curl http://localhost:8081/actuator/health
```

API Documentation: http://localhost:8081/swagger-ui.html

### As a Java SDK Library

```java
import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.services.AgentService;
import dev.sst.opencode.models.agent.AgentRequest;

// Create client
OpenCodeClient client = new OpenCodeClient("http://localhost:8765");

// Use services
AgentService agentService = client.getAgentService();
AgentRequest request = AgentRequest.builder()
    .name("my-agent")
    .modelId("claude-3-5-sonnet-20241022")
    .build();

String agentId = agentService.createAgent(request);
```

### With Spring Boot Auto-Configuration

Add to your `application.yml`:

```yaml
opencode:
  server:
    url: http://localhost:8765
    api-key: your-api-key
    timeout: 120000
```

Then inject the client:

```java
@Autowired
private OpenCodeClient openCodeClient;

@Autowired
private AgentService agentService;
```

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8081` | Bridge API server port |
| `OPENCODE_URL` | `http://localhost:8765` | OpenCode server URL |
| `OPENCODE_API_KEY` | - | API key for OpenCode (if required) |
| `OPENCODE_AUTO_START` | `false` | Auto-start OpenCode binary |
| `OPENCODE_BINARY_PATH` | `opencode` | Path to OpenCode binary |
| `API_SECURITY_ENABLED` | `false` | Enable API key authentication |
| `API_KEYS` | - | Comma-separated API keys |
| `CORS_ALLOWED_ORIGINS` | `*` | CORS allowed origins |

### application.yml Configuration

See `src/main/resources/application.yml` for full configuration options.

## 🐳 Docker Configuration

### Using with External OpenCode Server

```yaml
# docker-compose.yml
environment:
  - OPENCODE_URL=http://host.docker.internal:8765
  - OPENCODE_AUTO_START=false
```

### Bundling OpenCode Binary

Uncomment the OpenCode installation section in `Dockerfile`:

```dockerfile
RUN apt-get update && apt-get install -y curl && \
    curl -L https://github.com/opencode/opencode/releases/latest/download/opencode-linux-x64 -o /usr/local/bin/opencode && \
    chmod +x /usr/local/bin/opencode
```

Then set:

```yaml
environment:
  - OPENCODE_AUTO_START=true
  - OPENCODE_BINARY_PATH=/usr/local/bin/opencode
```

## 📋 Requirements

- **Java 17+** (Java 24 recommended for preview features)
- **Maven 3.9+**
- **Docker** (optional, for containerized deployment)
- **OpenCode Server** (must be running or auto-started)

## 🔧 Building from Source

```bash
# Full build with tests
mvn clean install

# Skip tests
mvn clean package -DskipTests

# Build Docker image
docker build -t opencode-all-in-one:latest .
```

## 📖 API Documentation

When running, visit:
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI Spec: http://localhost:8081/api-docs

## 🛠️ Development

### Project Structure

All source code is self-contained in this package:

- **SDK Core** (`dev.sst.opencode.*`) - Client, services, models
- **Spring Integration** (`dev.sst.opencode.spring`) - Auto-configuration
- **Bridge Application** (`dev.sst.opencode.bridge`) - OpenAI API bridge

### Customization

You can:
1. Modify the source code in `src/main/java`
2. Adjust configuration in `src/main/resources/application.yml`
3. Add custom controllers/services to the bridge
4. Extend the SDK with additional functionality

### Testing

```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=OpenCodeClientTest
```

## 🚨 Troubleshooting

### "Connection refused" to OpenCode

- Ensure OpenCode server is running on the configured URL
- Check `OPENCODE_URL` environment variable
- Try: `curl http://localhost:8765/health`

### Java version mismatch

- Ensure Java 17+ is installed
- For preview features, use: `java --enable-preview`

### Build failures

```bash
# Clean Maven cache
mvn clean

# Rebuild dependencies
mvn dependency:purge-local-repository
mvn clean install
```

## 📦 Deployment

### Standalone JAR

```bash
mvn clean package
java --enable-preview -jar target/opencode-all-in-one-*.jar
```

### Docker

```bash
docker build -t mycompany/opencode:1.0 .
docker run -p 8081:8081 mycompany/opencode:1.0
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: opencode-all-in-one
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: opencode
        image: opencode-all-in-one:latest
        ports:
        - containerPort: 8081
        env:
        - name: OPENCODE_URL
          value: "http://opencode-server:8765"
```

## 📄 License

See parent project LICENSE file.

## 🤝 Contributing

This is a self-contained package. For SDK updates, modify the source in this directory and rebuild.

## 🔗 Related Projects

- [OpenCode](https://github.com/opencode/opencode)
- [OpenCode Java SDK](../../)
- [Spring Chat Example](../spring-chat-app/)
- [OpenAI Bridge Example](../openai-api-bridge/)
