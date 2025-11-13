# Quick Start Guide

## 🚀 3-Step Launch

### 1. Clone/Copy this directory

```bash
# This entire directory is self-contained
cd examples/all-in-one
```

### 2. Build

```bash
mvn clean package -DskipTests
```

### 3. Run

```bash
# Option A: Use the launch script
./run.sh

# Option B: Run directly
java --enable-preview -jar target/opencode-all-in-one-*.jar

# Option C: Use Docker
docker-compose up --build
```

## ✅ Verify Installation

```bash
# Check health
curl http://localhost:8081/actuator/health

# Test chat endpoint
curl -X POST http://localhost:8081/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "claude-3-5-sonnet-20241022",
    "messages": [{"role": "user", "content": "Say hello!"}],
    "stream": false
  }'

# View API documentation
open http://localhost:8081/swagger-ui.html
```

## 📦 Import into Your Project

### Option 1: Copy Source Files

Copy the `src/` directory and `pom.xml` into your project.

### Option 2: Build and Install Locally

```bash
mvn clean install
```

Then add to your project's `pom.xml`:

```xml
<dependency>
    <groupId>dev.sst</groupId>
    <artifactId>opencode-all-in-one</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 3: Use as Fat JAR

```bash
# Build fat JAR
mvn clean package

# Include in your project's lib/ directory
cp target/opencode-all-in-one-*.jar ../your-project/lib/
```

## 🔧 Configuration

Create `.env` file:

```bash
cp .env.example .env
# Edit .env with your settings
```

Key settings:

```env
OPENCODE_URL=http://localhost:8765
SERVER_PORT=8081
```

## 🐳 Docker Quick Start

```bash
# Start everything
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

## 📚 Examples

### Java SDK Usage

```java
import dev.sst.opencode.client.OpenCodeClient;

OpenCodeClient client = new OpenCodeClient("http://localhost:8765");
var agents = client.getAgentService();
```

### OpenAI-Compatible API

```python
import openai

openai.api_base = "http://localhost:8081/v1"
openai.api_key = "not-needed"

response = openai.ChatCompletion.create(
    model="claude-3-5-sonnet-20241022",
    messages=[{"role": "user", "content": "Hello!"}]
)
```

## ❓ Common Issues

**Port already in use?**
```bash
export SERVER_PORT=8082
./run.sh
```

**OpenCode not running?**
```bash
# Start OpenCode first
opencode serve --port 8765

# Or configure auto-start
export OPENCODE_AUTO_START=true
```

**Java version issues?**
```bash
# Use Java 17+
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java-17
```

## 📖 Full Documentation

See [README.md](README.md) for complete documentation.

## 🎯 What's Included

This single package contains:
- ✅ Complete OpenCode Java SDK (47 source files)
- ✅ OpenAI-compatible REST API Bridge
- ✅ Spring Boot auto-configuration
- ✅ All dependencies bundled
- ✅ Docker support
- ✅ Launch scripts

No external dependencies required - just Java and Maven!
