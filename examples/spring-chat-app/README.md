# OpenCode Chat Example Application

A Spring Boot web application with **full OpenCode integration** for executing tasks, creating files, running commands, and more through a chat interface.

## Features

- **Real OpenCode Integration**: Actually connects to and controls OpenCode server (not a mock!)
- **Auto-launch Server**: Automatically starts OpenCode server when the app starts
- **Task Execution**: Create files, run shell commands, install dependencies - all through chat
- **Tool Execution Display**: Shows formatted output from all OpenCode tool executions
- **Session Management**: Create and maintain chat sessions with full context
- **Provider Selection**: Choose AI providers and models dynamically
- **Command Support**: Execute OpenCode commands (e.g., `/shell`, `/init`, `/help`)
- **WebSocket + REST**: Real-time updates with fallback to REST API

## Prerequisites

- Java 17+
- Maven 3.6+
- OpenCode installed (`~/.opencode/bin/opencode` or in PATH)
- The parent OpenCode Java SDK built locally

## Building and Running

1. First, build the parent SDK:
```bash
cd ../..
mvn clean install
```

2. Build the example application:
```bash
cd examples/spring-chat-app
mvn clean package
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Open your browser to: http://localhost:8080

## Usage

1. **Start the Application**: Run the app and OpenCode server will auto-start
2. **Select Provider/Model**: Choose from available providers and models
3. **Execute Tasks Through Chat**:
   - "Create a Python script that calculates fibonacci numbers"
   - "Run the script and show me the output"
   - "Install numpy and create a data analysis script"
   - "Create a web server in Node.js and test it"

### Example Interactions

**Creating and Running Code:**
```
You: Create a Python function to calculate prime numbers and test it
Assistant: I'll create a Python function to calculate prime numbers...

🔧 Tool Execution: create_file
Status: Completed
File: prime_numbers.py
[file content shown]

🔧 Tool Execution: shell
Status: Completed
$ python3 prime_numbers.py
[output shown]

The prime number calculator has been created and tested successfully!
```

**Commands**: Use commands like:
   - `/shell <command>` - Execute shell commands directly
   - `/init` - Initialize codebase analysis
   - `/new` - Start a new session
   - `/help` - Show available commands

## Architecture

### Backend Components

- **SimpleChatController**: REST endpoints for chat and server management
- **OpenCodeService**: Handles all OpenCode server interactions (reused from openai-api-bridge)
  - Auto-starts OpenCode server
  - Manages sessions
  - Sends prompts and commands
  - Formats responses with tool executions
- **WebSocketConfig**: STOMP over WebSocket configuration (optional, REST fallback available)

### Frontend

- **HTML/CSS**: Clean, responsive chat interface
- **JavaScript**: WebSocket client with SockJS and STOMP
- **Real-time Updates**: Bidirectional communication via WebSocket

## API Endpoints

### REST API
- `POST /api/server/start` - Start OpenCode server
- `POST /api/server/stop` - Stop OpenCode server
- `GET /api/server/status` - Check server status
- `POST /api/session/new` - Create new chat session
- `GET /api/providers` - List available providers
- `GET /api/models/{providerId}` - Get models for provider

### WebSocket
- **Endpoint**: `/ws` (with SockJS fallback)
- **Subscribe**: `/topic/messages` - Receive chat responses
- **Send**: `/app/chat` - Send chat messages

## Configuration

Edit `src/main/resources/application.properties`:

```properties
server.port=8080  # Change web app port
logging.level.dev.sst.opencode=DEBUG  # Adjust logging
```

## What You Can Do

With this chat interface, you can:
- **Create files**: Ask it to create any type of code file
- **Run commands**: Execute shell commands and see the output
- **Install packages**: Install npm, pip, or other packages as needed
- **Build projects**: Compile and run entire projects
- **Debug code**: Ask for help fixing errors
- **Refactor code**: Request improvements to existing code
- All with full visibility into what OpenCode is doing!

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# OpenCode Server Settings
opencode.server.url=http://localhost:8765  # OpenCode server URL
opencode.server.auto-start=true  # Auto-start server on app launch
opencode.server.binary-path=opencode  # Path to opencode binary
```

## Development

The application uses:
- Spring Boot 3.2.0
- Spring WebSocket for real-time communication
- Lombok for boilerplate reduction
- OpenCode Java SDK for server interaction

## License

This example is part of the OpenCode Java SDK project.