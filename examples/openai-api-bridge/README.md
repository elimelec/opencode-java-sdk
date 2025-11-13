# OpenAI API Bridge for OpenCode

A Spring Boot application that provides an OpenAI-compatible REST API interface for OpenCode, allowing you to use OpenCode as a powerful AI server with system access capabilities.

## Features

- **OpenAI API Compatibility**: Implements the OpenAI Chat Completions API format
- **Full Multi-Turn Support**: Captures ALL OpenCode actions including:
  - Text responses and reasoning
  - Tool executions (bash commands, file operations)
  - File creations and edits
  - Code snapshots and patches
  - Error handling and corrections
- **Streaming Support**: Real-time streaming responses via Server-Sent Events (SSE)
- **Session Management**: Maintains conversation context across requests
- **Docker Support**: Ready-to-deploy containerized solution
- **Auto-start OpenCode**: Optionally starts OpenCode server automatically
- **Swagger UI**: Interactive API documentation at `/swagger-ui.html`
- **CORS Support**: Configurable cross-origin resource sharing

## Why Use This?

1. **Universal Compatibility**: Works with any OpenAI API client library or tool
2. **System Access**: When properly configured in a container, OpenCode can:
   - Create and modify code
   - Run experiments
   - Execute system commands
   - Access development tools
3. **Security**: Run in a controlled, containerized environment
4. **Flexibility**: Use OpenCode with existing OpenAI-compatible tools and UIs

## Quick Start

### Using Maven

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8081`

### Using Docker

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build manually
docker build -t openai-api-bridge .
docker run -p 8081:8081 openai-api-bridge
```

## API Endpoints

### Chat Completions
```bash
POST /v1/chat/completions
```

Create a chat completion with OpenCode. The response includes ALL multi-turn actions performed by OpenCode.

**Example Request:**
```bash
curl -X POST http://localhost:8081/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4",
    "messages": [
      {"role": "system", "content": "You are a helpful coding assistant."},
      {"role": "user", "content": "Write a Python function to calculate fibonacci numbers and test it"}
    ],
    "temperature": 0.7,
    "max_tokens": 2000
  }'
```

**Example Response (with multi-turn actions):**
```json
{
  "id": "chatcmpl-...",
  "object": "chat.completion",
  "model": "gpt-4",
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "I'll create a Python function to calculate Fibonacci numbers and test it.\n\n### Tool Execution: create_file\n```\nStatus: Completed\nFile: fibonacci.py\ndef fibonacci(n):\n    if n <= 0:\n        return []\n    elif n == 1:\n        return [0]\n    elif n == 2:\n        return [0, 1]\n    \n    fib = [0, 1]\n    for i in range(2, n):\n        fib.append(fib[-1] + fib[-2])\n    return fib\n```\n\n### Tool Execution: shell\n```\nStatus: Completed\n$ python3 -c \"from fibonacci import fibonacci; print(fibonacci(10))\"\n[0, 1, 1, 2, 3, 5, 8, 13, 21, 34]\nExit code: 0\n```\n\nThe Fibonacci function has been created and tested successfully!"
    },
    "finish_reason": "stop"
  }],
  "usage": {
    "prompt_tokens": 45,
    "completion_tokens": 320,
    "total_tokens": 365
  }
}
```

**Streaming Example:**
```bash
curl -X POST http://localhost:8081/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4",
    "messages": [{"role": "user", "content": "Explain quantum computing"}],
    "stream": true
  }'
```

### List Models
```bash
GET /v1/models
```

Lists available models (mapped to OpenCode providers).

### Health Check
```bash
GET /v1/health
```

## Configuration

Configure the application via environment variables or `application.yml`:

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENCODE_URL` | OpenCode server URL | `http://localhost:8765` |
| `OPENCODE_API_KEY` | API key for OpenCode | (empty) |
| `OPENCODE_TIMEOUT` | Request timeout in ms | `120000` |
| `OPENCODE_AUTO_START` | Auto-start OpenCode server | `true` |
| `OPENCODE_BINARY_PATH` | Path to OpenCode binary | `opencode` |
| `API_SECURITY_ENABLED` | Enable API key authentication | `false` |
| `API_KEYS` | Comma-separated API keys | (empty) |
| `CORS_ALLOWED_ORIGINS` | CORS allowed origins | `*` |
| `SERVER_PORT` | Server port | `8081` |

## Model Mapping

The bridge supports flexible model specification:

### Direct Pass-Through (OpenRouter Style)
Use `provider/model` format to specify exact provider and model:
```json
{
  "model": "opencode/grok-code",
  "model": "anthropic/claude-3-opus",
  "model": "google/gemini-pro"
}
```

### Pass-Through for Unknown Models
Any unrecognized model name is passed through as-is to OpenCode:
```json
{
  "model": "llama-3.1-70b",  // Passed as-is to OpenCode
  "model": "mixtral-8x7b"     // Passed as-is to OpenCode
}
```

### OpenAI Compatibility Mapping
Common OpenAI models are automatically mapped for convenience:

| OpenAI Model | OpenCode Provider | OpenCode Model |
|--------------|-------------------|----------------|
| `gpt-4` | `opencode` | `grok-code` |
| `gpt-4-turbo` | `opencode` | `grok-code` |
| `gpt-3.5-turbo` | `opencode` | `qwen3-coder` |
| `gpt-3.5-turbo-16k` | `opencode` | `qwen3-coder` |

## Using with OpenAI Clients

### Python (openai library)
```python
import openai

openai.api_base = "http://localhost:8081/v1"
openai.api_key = "dummy"  # Required by library but not used

response = openai.ChatCompletion.create(
    model="gpt-4",
    messages=[
        {"role": "user", "content": "Write a hello world in Rust"}
    ]
)
print(response.choices[0].message.content)
```

### JavaScript/TypeScript
```javascript
import OpenAI from 'openai';

const openai = new OpenAI({
  baseURL: 'http://localhost:8081/v1',
  apiKey: 'dummy',
});

const completion = await openai.chat.completions.create({
  model: 'gpt-4',
  messages: [
    { role: 'user', content: 'Write a hello world in Go' }
  ],
});

console.log(completion.choices[0].message.content);
```

### Using with ChatGPT-like UIs

Many ChatGPT-like UIs support custom API endpoints:

1. **Open WebUI**: Set custom OpenAI API endpoint to `http://localhost:8081/v1`
2. **ChatGPT-Next-Web**: Configure API endpoint in settings
3. **BetterChatGPT**: Use custom API configuration

## Security Considerations

⚠️ **WARNING**: OpenCode with system access is powerful but potentially dangerous.

### Best Practices:

1. **Run in Containers**: Always run in Docker with limited permissions
2. **Network Isolation**: Use private networks, avoid public exposure
3. **Authentication**: Enable `API_SECURITY_ENABLED` in production
4. **Resource Limits**: Set memory and CPU limits in Docker
5. **Volume Mounts**: Only mount necessary directories
6. **Audit Logging**: Monitor API usage and system calls

### Example Secure Docker Compose:

```yaml
services:
  openai-bridge:
    build: .
    ports:
      - "127.0.0.1:8081:8081"  # Only localhost
    environment:
      - API_SECURITY_ENABLED=true
      - API_KEYS=${API_KEYS}
    volumes:
      - ./workspace:/workspace:ro  # Read-only
    mem_limit: 1g
    cpus: 2
    read_only: true
    security_opt:
      - no-new-privileges:true
```

## Development

### Project Structure
```
openai-api-bridge/
├── src/main/java/dev/sst/opencode/bridge/
│   ├── controller/      # REST controllers
│   ├── service/         # Business logic
│   ├── model/          # Request/Response models
│   └── config/         # Configuration classes
├── Dockerfile          # Container configuration
├── docker-compose.yml  # Orchestration
└── pom.xml            # Maven configuration
```

### Building from Source
```bash
# Clone the repository
git clone <repository-url>
cd openai-api-bridge

# Build
mvn clean package

# Run tests
mvn test

# Generate API documentation
mvn springdoc-openapi:generate
```

## Troubleshooting

### OpenCode Server Not Starting
- Check if `opencode` binary is in PATH
- Verify port 8765 is not in use
- Check logs: `docker-compose logs openai-bridge`

### Connection Refused
- Ensure OpenCode server is running
- Check `OPENCODE_URL` configuration
- Verify network connectivity

### Timeout Errors
- Increase `OPENCODE_TIMEOUT` value
- Check OpenCode server performance
- Monitor system resources

## License

[Your License]

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.

## Support

For issues and questions:
- GitHub Issues: [Link to issues]
- Documentation: [Link to docs]