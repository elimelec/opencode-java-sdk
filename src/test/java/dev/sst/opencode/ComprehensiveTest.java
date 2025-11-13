package dev.sst.opencode;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Comprehensive test demonstrating all new SDK functionality Note: These tests are
 * disabled by default as they require a running OpenCode server
 */
@Disabled("Requires running OpenCode server")
public class ComprehensiveTest {

	private OpenCodeClient client;

	@BeforeEach
	public void setUp() {
		client = OpenCodeClient.builder().baseUrl("http://localhost:8080").build();
	}

	@Test
	public void testAppService() {
		// Get OpenAPI documentation
		String openApiDoc = client.getApp().getOpenApiDoc();
		assertNotNull(openApiDoc);
		assertTrue(openApiDoc.contains("openapi"));
	}

	@Test
	public void testCommandService() {
		// List all commands
		List<Command> commands = client.getCommands().listCommands();
		assertNotNull(commands);
		assertFalse(commands.isEmpty());

		// Get specific command
		if (!commands.isEmpty()) {
			Command firstCommand = commands.get(0);
			Command retrieved = client.getCommands().getCommand(firstCommand.getName());
			assertEquals(firstCommand.getName(), retrieved.getName());
		}

		// List all agents
		List<Agent> agents = client.getCommands().listAgents();
		assertNotNull(agents);
		assertFalse(agents.isEmpty());

		// Get specific agent
		if (!agents.isEmpty()) {
			Agent firstAgent = agents.get(0);
			Agent retrieved = client.getCommands().getAgent(firstAgent.getId());
			assertEquals(firstAgent.getId(), retrieved.getId());
		}
	}

	@Test
	public void testToolService() {
		// List tool IDs
		List<String> toolIds = client.getTools().listToolIds();
		assertNotNull(toolIds);
		assertFalse(toolIds.isEmpty());

		// List tools for a specific provider/model
		List<Tool> tools = client.getTools().listTools("anthropic", "claude-3-5-sonnet-latest");
		assertNotNull(tools);

		// Register a custom tool (experimental)
		ToolRegisterRequest registerRequest = ToolRegisterRequest.builder()
			.id("custom-tool")
			.name("Custom Tool")
			.description("A custom HTTP callback tool")
			.callbackUrl("http://localhost:9000/callback")
			.build();

		Tool registeredTool = client.getTools().registerTool(registerRequest);
		assertNotNull(registeredTool);
		assertEquals("custom-tool", registeredTool.getId());

		// Get the registered tool
		Tool retrieved = client.getTools().getTool("custom-tool");
		assertEquals(registeredTool.getId(), retrieved.getId());

		// Unregister the tool
		client.getTools().unregisterTool("custom-tool");
	}

	@Test
	public void testTuiService() {
		// Append text to prompt
		client.getTui().appendPrompt("Hello from Java SDK");

		// Clear the prompt
		client.getTui().clearPrompt();

		// Show a toast notification
		client.getTui().showToast("Test notification", "info", 3000);

		// Execute a TUI command
		client.getTui().executeCommand("/help");

		// Open various dialogs
		client.getTui().openHelp();
		client.getTui().openSessions();
		client.getTui().openThemes();
		client.getTui().openModels();

		// Submit prompt
		client.getTui().submitPrompt();
	}

	@Test
	public void testLogService() {
		// Log various levels
		client.getLogs().debug("Debug message", "TestSuite");
		client.getLogs().info("Info message", "TestSuite");
		client.getLogs().warn("Warning message", "TestSuite");
		client.getLogs().error("Error message", "TestSuite");

		// Log with custom request
		LogRequest logRequest = LogRequest.builder()
			.level("info")
			.message("Custom log entry")
			.service("TestSuite")
			.timestamp(System.currentTimeMillis())
			.build();

		client.getLogs().log(logRequest);
	}

	@Test
	public void testExtendedSessionOperations() {
		// Create a session
		Session session = client.getSessions()
			.createSession(SessionCreateRequest.builder().title("Test Session").build());
		assertNotNull(session);

		String sessionId = session.getId();

		// Get session children
		List<Session> children = client.getSessions().getSessionChildren(sessionId);
		assertNotNull(children);

		// Execute shell command in session
		ShellRequest shellRequest = ShellRequest.builder().command("echo 'Hello from Java SDK'").timeout(5000L).build();

		Message shellResult = client.getSessions().executeShellCommand(sessionId, shellRequest);
		assertNotNull(shellResult);

		// Generate session summary
		SessionSummary summary = client.getSessions()
			.summarizeSession(sessionId, "anthropic", "claude-3-5-sonnet-latest");
		assertNotNull(summary);
		System.out.println("Summary: " + summary.getSummary());

		// Respond to permission request (if any)
		// This would typically happen in response to a permission event
		PermissionResponse permissionResponse = PermissionResponse.builder()
			.granted(true)
			.reason("Approved by test")
			.remember(false)
			.build();

		// Note: This would normally be called with a real permission ID
		// client.getSessions().respondToPermission(sessionId, "permission_123",
		// permissionResponse);

		// Clean up
		client.getSessions().deleteSession(sessionId);
	}

	@Test
	public void testFullWorkflow() {
		// Note: App initialization endpoints don't exist in current OpenCode server

		// Create a session
		Session session = client.getSessions()
			.createSession(SessionCreateRequest.builder().title("Full Workflow Test").build());

		String sessionId = session.getId();

		// Log the session creation
		client.getLogs().info("Created session: " + sessionId, "TestWorkflow");

		// Send a prompt
		PromptRequest prompt = PromptRequest.ofText("Write a hello world in Python", "anthropic",
				"claude-3-5-sonnet-latest");

		Message response = client.getSessions().sendPrompt(sessionId, prompt);
		assertNotNull(response);

		// Execute a shell command
		Message shellResult = client.getSessions()
			.executeShellCommand(sessionId, ShellRequest.builder().command("ls -la").build());
		assertNotNull(shellResult);

		// Generate summary
		SessionSummary summary = client.getSessions()
			.summarizeSession(sessionId, "anthropic", "claude-3-5-sonnet-latest");
		assertNotNull(summary);

		// Show toast in TUI
		client.getTui().showToast("Workflow completed!", "success", 5000);

		// Clean up
		client.getSessions().deleteSession(sessionId);
		client.getLogs().info("Deleted session: " + sessionId, "TestWorkflow");
	}

}