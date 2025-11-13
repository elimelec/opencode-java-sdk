# OpenCode Java SDK

A comprehensive Java SDK for the OpenCode API with Spring Boot integration, Swagger documentation, and Server-Sent Events (SSE) support.

For contributor onboarding, review the [Repository Guidelines](AGENTS.md).

## Features

- ✅ Full coverage of OpenCode API endpoints
- ✅ Spring Boot auto-configuration
- ✅ `OpenCodeRestTemplate` for Spring developers
- ✅ Swagger/OpenAPI annotations for documentation
- ✅ Server-Sent Events (SSE) support for real-time updates
- ✅ Reactive streams with Project Reactor
- ✅ Type-safe models with builders
- ✅ Comprehensive error handling
- ✅ Health indicator for Spring Boot Actuator

## Installation

### Maven

```xml
<dependency>
    <groupId>dev.sst</groupId>
    <artifactId>opencode-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'dev.sst:opencode-java-sdk:1.0.0'
```

## Quick Start

### 1. Spring Boot Application

Add the dependency and configure in `application.yml`:

```yaml
opencode:
  base-url: http://localhost:8080
  working-directory: /path/to/your/project
  # Optional settings:
  api-key: your-api-key-here
  timeout: 30000
  max-retries: 3
  event-stream:
    enabled: true
    reconnect-delay: 5000
```

Then inject and use `OpenCodeRestTemplate`:

```java
@Service
public class MyService {
    
    @Autowired
    private OpenCodeRestTemplate openCode;
    
    public void generateCode() {
        // Create a session
        Session session = openCode.createSession("My Coding Session");
        
        // Send a prompt
        Message response = openCode.sendPrompt(
            session.getId(),
            "Write a REST API controller in Spring Boot",
            "anthropic",
            "claude-3-5-sonnet-latest"
        );
        
        // Process the response
        response.getParts().forEach(part -> {
            if (part instanceof Message.TextPart) {
                System.out.println(((Message.TextPart) part).getText());
            }
        });
    }
}
```

### 2. Standalone Java Application

```java
import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.models.*;

public class Example {
    public static void main(String[] args) {
        // Create client
        OpenCodeClient client = OpenCodeClient.builder()
            .baseUrl("http://localhost:8080")
            .workingDirectory("/path/to/project")
            .build();
        
        // Create a session
        Session session = client.sessions().create("My Session");
        
        // Send a prompt
        Message response = client.sessions().sendPrompt(
            session.getId(),
            PromptRequest.ofText(
                "Write hello world in Python",
                "anthropic",
                "claude-3-5-sonnet-latest"
            )
        );
        
        // Clean up
        client.close();
    }
}
```

## Advanced Usage

### Event Streaming (SSE)

Subscribe to real-time events from the OpenCode server:

```java
@Service
public class EventListenerService {
    
    @Autowired
    private OpenCodeRestTemplate openCode;
    
    @PostConstruct
    public void startListening() {
        openCode.subscribeToEvents()
            .filter(event -> event.isMessageEvent())
            .subscribe(event -> {
                log.info("Message event: {}", event.getType());
                // Handle the event
            });
    }
}
```

### Complex Prompts with File Context

```java
PromptRequest request = PromptRequest.builder()
    .text(List.of(
        TextContent.builder()
            .value("Please review this code and optimize it")
            .build()
    ))
    .files(List.of(
        FileContent.builder()
            .path("src/main/java/MyClass.java")
            .ranges(List.of(
                Range.builder().start(10).end(50).build()
            ))
            .build()
    ))
    .model(ModelConfig.builder()
        .providerId("anthropic")
        .modelId("claude-3-5-sonnet-latest")
        .build())
    .agent("code-review")
    .build();

Message response = openCode.sendPrompt(sessionId, request);
```

### File Operations

```java
// Read a file
FileContent content = openCode.readFile("/path/to/file.txt");

// Search for text in files
List<SearchMatch> matches = openCode.searchText("TODO");

// Find files by pattern
List<String> javaFiles = openCode.findFiles("*.java");

// List directory contents
List<FileNode> files = openCode.listFiles("/src");
```

### Session Management

```java
// List all sessions
List<Session> sessions = openCode.listSessions();

// Update session title
Session updated = openCode.updateSession(sessionId, "New Title");

// Share a session
Session shared = openCode.sessions().shareSession(sessionId);
String shareUrl = shared.getShare().getUrl();

// Revert to previous message
openCode.sessions().revertMessage(sessionId, messageId, partId);
```

## Configuration

### Environment Variables

- `OPENCODE_BASE_URL` - Base URL of the OpenCode server
- `OPENCODE_API_KEY` - API key for authentication
- `OPENCODE_WORKING_DIR` - Default working directory

### Programmatic Configuration

```java
OpenCodeConfig config = OpenCodeConfig.builder()
    .baseUrl("http://localhost:8080")
    .apiKey("your-api-key")
    .workingDirectory("/path/to/project")
    .timeout(30000)
    .maxRetries(3)
    .build();

OpenCodeClient client = new OpenCodeClient(config);
```

## Spring Boot Features

### Auto-Configuration

The SDK automatically configures when on the classpath. Disable with:

```yaml
opencode:
  enabled: false
```

### Health Indicator

Automatically provides health check at `/actuator/health`:

```json
{
  "opencode": {
    "status": "UP",
    "details": {
      "baseUrl": "http://localhost:8080",
      "version": "1.0.0",
      "providers": ["anthropic", "openai"]
    }
  }
}
```

### Metrics

When Spring Boot Actuator is present, the SDK exposes metrics:

- `opencode.sessions.created` - Counter of sessions created
- `opencode.messages.sent` - Counter of messages sent
- `opencode.api.requests` - Timer for API request duration

## API Documentation

When using Spring Boot with SpringDoc OpenAPI, the SDK's endpoints are automatically documented at `/swagger-ui.html`.

## Error Handling

The SDK provides comprehensive error handling:

```java
try {
    Message response = openCode.sendPrompt(sessionId, request);
} catch (OpenCodeException e) {
    if (e instanceof AuthenticationException) {
        // Handle auth error
    } else if (e instanceof RateLimitException) {
        // Handle rate limit
    } else if (e instanceof SessionNotFoundException) {
        // Handle missing session
    }
}
```

## Testing

The SDK includes testing utilities:

```java
@SpringBootTest
@AutoConfigureMockMvc
class MyServiceTest {
    
    @MockBean
    private OpenCodeRestTemplate openCode;
    
    @Test
    void testCodeGeneration() {
        // Mock the OpenCode responses
        Session mockSession = Session.builder()
            .id("session_123")
            .title("Test Session")
            .build();
        
        when(openCode.createSession(any())).thenReturn(mockSession);
        
        // Test your service
        // ...
    }
}
```

## Requirements

- Java 11 or higher
- Spring Boot 3.x (for Spring integration features)
- OpenCode server running (default: http://localhost:8080)

## Building from Source

```bash
git clone https://github.com/elimelec/opencode-java-sdk.git
cd opencode-java-sdk
mvn clean install
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License - see LICENSE file for details.

## Support

For issues and questions:
- GitHub Issues: https://github.com/elimelec/opencode-java-sdk/issues
- OpenCode Documentation: https://docs.opencode.dev

## Example Projects

See the `examples/` directory for complete example projects:

- `spring-boot-example/` - Spring Boot web application
- `cli-example/` - Command-line application
- `reactive-example/` - Reactive streams with WebFlux
