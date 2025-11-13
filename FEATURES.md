# OpenCode Java SDK - Complete Feature List

## Overview
The OpenCode Java SDK provides comprehensive access to all OpenCode API endpoints, supporting 100% of the available functionality.

## Core Services

### 1. Session Service
- Create, list, get, update, and delete sessions
- Send prompts and execute commands
- Share/unshare sessions
- Initialize sessions with AGENTS.md
- Abort running sessions
- Revert and unrevert messages
- **NEW:** Get session children
- **NEW:** Generate session summaries
- **NEW:** Execute shell commands in sessions
- **NEW:** Respond to permission requests

### 2. File Service
- Read files and directories
- List files with status (git-like)
- Search text in files (ripgrep)
- Find files by name pattern
- Find workspace symbols

### 3. Configuration Service
- Get configuration info
- List available providers
- Get working directory
- Set authentication credentials

### 4. Event Service
- Subscribe to server-sent events (SSE)
- Filter events by type
- Reactive streams with Project Reactor
- Callback-based event handling

### 5. App Service (NEW)
- Get application information
- Initialize application
- Retrieve OpenAPI documentation

### 6. Command Service (NEW)
- List all available commands
- Get specific command details
- List all agents/modes
- Get specific agent information

### 7. Tool Service (NEW)
- Register HTTP callback tools
- List all tool IDs
- List tools with JSON schemas
- Get specific tool details
- Unregister dynamic tools

### 8. TUI Service (NEW)
- Append text to prompt
- Submit and clear prompts
- Open dialogs (help, sessions, themes, models)
- Execute TUI commands
- Show toast notifications
- Control TUI remotely

### 9. Log Service (NEW)
- Write log entries to server
- Support for debug, info, warn, error levels
- Custom log metadata
- Source tracking

## Usage Examples

### Basic Session Operations
```java
OpenCodeClient client = OpenCodeClient.builder()
    .baseUrl("http://localhost:8080")
    .build();

// Create a session
Session session = client.getSessions().createSession(
    SessionCreateRequest.builder()
        .title("My Session")
        .build()
);

// Send a prompt
Message response = client.getSessions().sendPrompt(
    session.getId(),
    PromptRequest.builder()
        .text("Write hello world in Python")
        .providerID("anthropic")
        .modelID("claude-3-5-sonnet-latest")
        .build()
);
```

### Application Management
```java
// Get app info
AppInfo appInfo = client.getApp().getAppInfo();

// Initialize if needed
if (!appInfo.isInitialized()) {
    client.getApp().initializeApp();
}

// Get OpenAPI documentation
String openApiDoc = client.getApp().getOpenApiDoc();
```

### Command and Agent Discovery
```java
// List all commands
List<Command> commands = client.getCommands().listCommands();

// List all agents
List<Agent> agents = client.getCommands().listAgents();
```

### Tool Registration
```java
// Register a custom HTTP callback tool
Tool tool = client.getTools().registerTool(
    ToolRegisterRequest.builder()
        .id("my-tool")
        .name("My Custom Tool")
        .callbackUrl("http://localhost:9000/callback")
        .build()
);

// List all available tools
List<String> toolIds = client.getTools().listToolIds();
```

### TUI Control
```java
// Show notifications
client.getTui().showToast("Operation complete!", "success", 5000);

// Control the terminal UI
client.getTui().appendPrompt("Hello from Java");
client.getTui().submitPrompt();

// Open dialogs
client.getTui().openHelp();
client.getTui().openModels();
```

### Server Logging
```java
// Log messages to the server
client.getLogs().info("Application started", "MyApp");
client.getLogs().error("Something went wrong", "MyApp");

// Custom log entry
client.getLogs().log(
    LogRequest.builder()
        .level("debug")
        .message("Detailed debug info")
        .source("MyApp")
        .metadata(Map.of("key", "value"))
        .build()
);
```

### Extended Session Features
```java
// Execute shell commands
Message result = client.getSessions().executeShellCommand(
    sessionId,
    ShellRequest.builder()
        .command("npm test")
        .timeout(30000L)
        .build()
);

// Generate session summary
SessionSummary summary = client.getSessions().summarizeSession(
    sessionId,
    "anthropic",
    "claude-3-5-sonnet-latest"
);

// Handle permissions
client.getSessions().respondToPermission(
    sessionId,
    permissionId,
    PermissionResponse.builder()
        .granted(true)
        .reason("Approved")
        .build()
);
```

## Spring Boot Integration

The SDK includes full Spring Boot integration with auto-configuration:

```java
@RestController
public class MyController {
    @Autowired
    private OpenCodeRestTemplate openCode;
    
    @GetMapping("/test")
    public String test() {
        openCode.logInfo("Test endpoint called");
        openCode.showToast("API called", "info");
        return "OK";
    }
}
```

## API Coverage

### Implemented Endpoints (45/45 - 100%)

#### Core Operations
- ✅ `/doc` - OpenAPI documentation
- ✅ `/config` - Configuration info
- ✅ `/config/providers` - List providers
- ✅ `/path` - Working directory
- ✅ `/app` - Application info
- ✅ `/app/init` - Initialize application

#### Authentication
- ✅ `/auth/:id` - Set credentials

#### Logging & Events
- ✅ `/event` - SSE stream
- ✅ `/log` - Write log entries

#### Sessions
- ✅ `/session` - CRUD operations
- ✅ `/session/:id` - Get/update/delete
- ✅ `/session/:id/children` - Get children
- ✅ `/session/:id/message` - Messages
- ✅ `/session/:id/command` - Commands
- ✅ `/session/:id/shell` - Shell commands
- ✅ `/session/:id/init` - Initialize
- ✅ `/session/:id/abort` - Abort
- ✅ `/session/:id/share` - Share/unshare
- ✅ `/session/:id/summarize` - Summarize
- ✅ `/session/:id/revert` - Revert
- ✅ `/session/:id/unrevert` - Unrevert
- ✅ `/session/:id/permissions/:permissionID` - Permissions

#### Commands & Agents
- ✅ `/command` - List commands
- ✅ `/agent` - List agents

#### Tools (Experimental)
- ✅ `/experimental/tool/register` - Register tool
- ✅ `/experimental/tool/ids` - List IDs
- ✅ `/experimental/tool` - List tools

#### Search & Files
- ✅ `/find` - Text search
- ✅ `/find/file` - Find files
- ✅ `/find/symbol` - Find symbols
- ✅ `/file` - Read files
- ✅ `/file/status` - File status

#### TUI Control
- ✅ `/tui/append-prompt` - Append text
- ✅ `/tui/submit-prompt` - Submit
- ✅ `/tui/clear-prompt` - Clear
- ✅ `/tui/open-help` - Open help
- ✅ `/tui/open-sessions` - Open sessions
- ✅ `/tui/open-themes` - Open themes
- ✅ `/tui/open-models` - Open models
- ✅ `/tui/execute-command` - Execute command
- ✅ `/tui/show-toast` - Show toast
- ✅ `/tui/control/next` - Get control request
- ✅ `/tui/control/response` - Submit response

## Requirements

- Java 24 or higher
- Maven 3.6+
- OpenCode server running (default: http://localhost:8080)

## Installation

```xml
<dependency>
    <groupId>dev.sst</groupId>
    <artifactId>opencode-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

### Environment Variables
- `OPENCODE_BASE_URL` - Server URL (default: http://localhost:8080)
- `OPENCODE_API_KEY` - API key for authentication
- `OPENCODE_WORKING_DIR` - Working directory

### Programmatic Configuration
```java
OpenCodeClient client = OpenCodeClient.builder()
    .baseUrl("http://localhost:8080")
    .apiKey("your-api-key")
    .workingDirectory("/path/to/project")
    .timeout(30000)
    .maxRetries(3)
    .build();
```

## Testing

Run the comprehensive test suite:
```bash
mvn test -Dtest=ComprehensiveTest
```

Note: Tests require a running OpenCode server.

## License

MIT