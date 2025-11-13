package dev.sst.opencode.examples.chat.service;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OpenCodeService {

	private static final Logger logger = LoggerFactory.getLogger(OpenCodeService.class);

	private OpenCodeClient client;

	private final Map<String, String> sessionMapping = new ConcurrentHashMap<>();

	private Process opencodeProcess;

	@Value("${opencode.server.url:http://localhost:8765}")
	private String opencodeUrl;

	@Value("${opencode.server.api-key:}")
	private String apiKey;

	@Value("${opencode.server.timeout:120000}")
	private int timeout;

	@Value("${opencode.server.auto-start:true}")
	private boolean autoStart;

	@Value("${opencode.server.binary-path:opencode}")
	private String binaryPath;

	@Value("${opencode.server.working-directory:/tmp/opencode-workspace}")
	private String workingDirectory;

	@PostConstruct
	public void init() {
		if (autoStart) {
			startOpenCodeServer();
		}

		client = OpenCodeClient.builder().baseUrl(opencodeUrl).apiKey(apiKey).timeout(timeout).build();

		logger.info("OpenCode service initialized with URL: {}", opencodeUrl);
	}

	@PreDestroy
	public void cleanup() {
		if (client != null) {
			client.close();
		}
		if (opencodeProcess != null && opencodeProcess.isAlive()) {
			logger.info("Stopping OpenCode server...");
			opencodeProcess.destroyForcibly();
		}
	}

	public boolean startOpenCodeServer() {
		try {
			if (opencodeProcess != null && opencodeProcess.isAlive()) {
				logger.info("OpenCode server already running");
				return true;
			}

			// Extract port from URL (default to 8765)
			int port = 8765;
			try {
				java.net.URL url = new java.net.URL(opencodeUrl);
				port = url.getPort() > 0 ? url.getPort() : 8765;
			}
			catch (Exception e) {
				logger.warn("Failed to parse port from URL, using default 8765");
			}

			// Create working directory if it doesn't exist
			java.io.File workDir = new java.io.File(workingDirectory);
			if (!workDir.exists()) {
				workDir.mkdirs();
				logger.info("Created working directory: {}", workingDirectory);
			}

			logger.info("Starting OpenCode server with binary: {} on port {} in directory: {}", binaryPath, port,
					workingDirectory);
			ProcessBuilder pb = new ProcessBuilder(binaryPath, "serve", "--port", String.valueOf(port));
			pb.directory(workDir); // Set working directory for the process
			pb.inheritIO();
			opencodeProcess = pb.start();

			// Wait for server to start
			Thread.sleep(3000);

			// Reinitialize client after server start
			client = OpenCodeClient.builder().baseUrl(opencodeUrl).apiKey(apiKey).timeout(timeout).build();

			logger.info("OpenCode server started successfully");
			return true;
		}
		catch (Exception e) {
			logger.error("Failed to start OpenCode server: {}", e.getMessage());
			return false;
		}
	}

	public boolean stopOpenCodeServer() {
		try {
			if (opencodeProcess != null && opencodeProcess.isAlive()) {
				opencodeProcess.destroyForcibly();
				logger.info("OpenCode server stopped");
				return true;
			}
			return false;
		}
		catch (Exception e) {
			logger.error("Failed to stop OpenCode server: {}", e.getMessage());
			return false;
		}
	}

	public boolean isServerRunning() {
		try {
			// Try to get config to check if server is responding
			ConfigInfo config = client.getConfiguration().getConfig();
			return config != null;
		}
		catch (Exception e) {
			return false;
		}
	}

	public String getServerUrl() {
		return opencodeUrl;
	}

	public String createSession(String title) {
		try {
			SessionCreateRequest request = SessionCreateRequest.builder()
				.title(title != null ? title : "Chat Session")
				.build();

			Session session = client.getSessions().createSession(request);
			return session.getId();
		}
		catch (Exception e) {
			logger.error("Failed to create session", e);
			throw new RuntimeException("Failed to create session: " + e.getMessage());
		}
	}

	public Map<String, Object> getProviders() {
		try {
			ProvidersResponse response = client.getConfiguration().listProviders();
			List<Map<String, String>> providers = new ArrayList<>();

			if (response != null && response.getProviders() != null) {
				for (var provider : response.getProviders()) {
					Map<String, String> providerInfo = new HashMap<>();
					providerInfo.put("id", provider.getId());
					providerInfo.put("name", provider.getName() != null ? provider.getName() : provider.getId());
					providers.add(providerInfo);
				}
			}

			// Add default if no providers found
			if (providers.isEmpty()) {
				Map<String, String> defaultProvider = new HashMap<>();
				defaultProvider.put("id", "opencode");
				defaultProvider.put("name", "OpenCode");
				providers.add(defaultProvider);
			}

			Map<String, Object> result = new HashMap<>();
			result.put("success", true);
			result.put("providers", providers);
			return result;
		}
		catch (Exception e) {
			logger.error("Failed to get providers", e);
			Map<String, Object> result = new HashMap<>();
			result.put("success", false);
			result.put("error", e.getMessage());
			return result;
		}
	}

	public Map<String, Object> getModels(String providerId) {
		try {
			// Get models from OpenCode API
			ProvidersResponse response = client.getConfiguration().listProviders();
			List<Map<String, String>> models = new ArrayList<>();

			if (response != null && response.getProviders() != null) {
				for (var provider : response.getProviders()) {
					if (providerId.equals(provider.getId()) && provider.getModels() != null) {
						// Extract models for this provider
						for (var entry : provider.getModels().entrySet()) {
							var model = entry.getValue();
							Map<String, String> modelInfo = new HashMap<>();
							modelInfo.put("id", model.getId());
							modelInfo.put("name", model.getName() != null ? model.getName() : model.getId());
							models.add(modelInfo);
						}
						break;
					}
				}
			}

			// Fallback to default models if none found
			if (models.isEmpty()) {
				models.add(Map.of("id", "claude-sonnet-4-5", "name", "Claude Sonnet 4.5"));
				models.add(Map.of("id", "grok-code", "name", "Grok Code"));
			}

			Map<String, Object> result = new HashMap<>();
			result.put("success", true);
			result.put("models", models);
			return result;
		}
		catch (Exception e) {
			logger.error("Failed to get models", e);
			Map<String, Object> result = new HashMap<>();
			result.put("success", false);
			result.put("error", e.getMessage());
			return result;
		}
	}

	public Message sendMessage(String sessionId, String content, String providerId, String modelId) {
		try {
			// Check for commands
			if (content.startsWith("/")) {
				return executeCommand(sessionId, content);
			}

			// Regular prompt
			PromptRequest request = PromptRequest.ofText(content, providerId != null ? providerId : "opencode",
					modelId != null ? modelId : "grok-code");

			Message message = client.getSessions().sendPrompt(sessionId, request);

			// Poll until all tools are completed
			return waitForMessageCompletion(sessionId, message);
		}
		catch (Exception e) {
			logger.error("Failed to send message", e);
			throw new RuntimeException("Failed to send message: " + e.getMessage());
		}
	}

	private Message waitForMessageCompletion(String sessionId, Message message) {
		try {
			int maxAttempts = 60; // 60 seconds max wait time
			int attempt = 0;

			while (attempt < maxAttempts && hasIncompleteTool(message)) {
				Thread.sleep(1000); // Wait 1 second
				message = client.getSessions().getMessage(sessionId, message.getInfo().getId());
				attempt++;
				logger.debug("Polling message status... attempt {}/{}", attempt, maxAttempts);
			}

			if (attempt >= maxAttempts) {
				logger.warn("Message polling timed out after {} seconds", maxAttempts);
			}

			return message;
		}
		catch (Exception e) {
			logger.error("Error while waiting for message completion", e);
			return message; // Return what we have
		}
	}

	private boolean hasIncompleteTool(Message message) {
		if (message == null || message.getParts() == null) {
			return false;
		}

		for (Message.MessagePart part : message.getParts()) {
			if (part instanceof Message.ToolPart) {
				Message.ToolPart toolPart = (Message.ToolPart) part;
				Message.ToolState state = toolPart.getState();
				if (state instanceof Message.ToolStatePending || state instanceof Message.ToolStateRunning) {
					return true;
				}
			}
		}

		return false;
	}

	public String formatMessageResponse(Message message) {
		if (message == null || message.getParts() == null) {
			return "";
		}

		StringBuilder formatted = new StringBuilder();

		for (Message.MessagePart part : message.getParts()) {
			String partText = formatMessagePart(part);
			if (partText != null && !partText.isEmpty()) {
				formatted.append(partText);
			}
		}

		return formatted.toString();
	}

	private String formatMessagePart(Message.MessagePart part) {
		if (part == null)
			return "";

		StringBuilder result = new StringBuilder();

		if (part instanceof Message.TextPart) {
			Message.TextPart textPart = (Message.TextPart) part;
			result.append(textPart.getText()).append("\n");

		}
		else if (part instanceof Message.ToolPart) {
			Message.ToolPart toolPart = (Message.ToolPart) part;
			result.append("\n**Tool Execution:** `").append(toolPart.getTool()).append("`\n");

			if (toolPart.getState() != null) {
				result.append("```\n");
				result.append(formatToolState(toolPart.getState()));
				result.append("```\n");
			}

		}
		else if (part instanceof Message.FilePart) {
			Message.FilePart filePart = (Message.FilePart) part;
			result.append("\n**File:** ").append(filePart.getFilename());
			if (filePart.getMime() != null) {
				result.append(" (").append(filePart.getMime()).append(")");
			}
			result.append("\n");

		}
		else if (part instanceof Message.SnapshotPart) {
			Message.SnapshotPart snapshotPart = (Message.SnapshotPart) part;
			result.append("\n**Code Snapshot:**\n");
			result.append("```\n");
			result.append(snapshotPart.getSnapshot());
			result.append("```\n");

		}
		else if (part instanceof Message.PatchPart) {
			Message.PatchPart patchPart = (Message.PatchPart) part;
			result.append("\n**Files Modified:**\n");
			if (patchPart.getFiles() != null) {
				for (String file : patchPart.getFiles()) {
					result.append("- ").append(file).append("\n");
				}
			}
		}

		return result.toString();
	}

	private String formatToolState(Message.ToolState state) {
		if (state == null)
			return "";

		StringBuilder result = new StringBuilder();

		if (state instanceof Message.ToolStatePending) {
			result.append("Status: Pending\n");

		}
		else if (state instanceof Message.ToolStateRunning) {
			Message.ToolStateRunning running = (Message.ToolStateRunning) state;
			result.append("Status: Running\n");
			if (running.getTitle() != null) {
				result.append(running.getTitle()).append("\n");
			}

		}
		else if (state instanceof Message.ToolStateCompleted) {
			Message.ToolStateCompleted completed = (Message.ToolStateCompleted) state;
			result.append("Status: Completed\n");

			if (completed.getInput() != null) {
				String inputStr = formatInput(completed.getInput());
				if (!inputStr.isEmpty()) {
					result.append(inputStr);
				}
			}

			if (completed.getOutput() != null) {
				result.append(completed.getOutput()).append("\n");
			}

		}
		else if (state instanceof Message.ToolStateError) {
			Message.ToolStateError error = (Message.ToolStateError) state;
			result.append("Status: Error\n");
			if (error.getError() != null) {
				result.append("Error: ").append(error.getError()).append("\n");
			}
		}

		return result.toString();
	}

	private String formatInput(Map<String, Object> input) {
		if (input == null)
			return "";

		StringBuilder result = new StringBuilder();

		// Handle common input types
		if (input.containsKey("command")) {
			result.append("$ ").append(input.get("command")).append("\n");
		}
		else if (input.containsKey("path") || input.containsKey("file_path")) {
			String path = (String) input.getOrDefault("path", input.get("file_path"));
			result.append("File: ").append(path).append("\n");
			if (input.containsKey("content")) {
				String content = input.get("content").toString();
				if (content.length() <= 1000) {
					result.append(content).append("\n");
				}
			}
		}
		else if (input.containsKey("content")) {
			String content = input.get("content").toString();
			if (content.length() <= 500) {
				result.append(content).append("\n");
			}
		}

		return result.toString();
	}

	private Message executeCommand(String sessionId, String command) {
		try {
			String[] parts = command.split("\\s+", 2);
			String cmd = parts[0];
			String args = parts.length > 1 ? parts[1] : "";

			switch (cmd) {
				case "/shell":
					if (!args.isEmpty()) {
						ShellRequest shellRequest = ShellRequest.builder().command(args).agent("shell").build();
						return client.getSessions().executeShellCommand(sessionId, shellRequest);
					}
					break;

				case "/init":
					client.getSessions().initializeSession(sessionId, "opencode", "grok-code");
					Message.TextPart initResponse = new Message.TextPart();
					initResponse.setText("Session initialized for codebase analysis.");
					Message initMsg = new Message();
					initMsg.setParts(List.of(initResponse));
					return initMsg;

				case "/help":
					Message.TextPart helpResponse = new Message.TextPart();
					helpResponse.setText("Available commands:\n" + "/shell <command> - Execute shell command\n"
							+ "/init - Initialize codebase analysis\n" + "/new - Create new session\n"
							+ "/help - Show this help message");
					Message helpMsg = new Message();
					helpMsg.setParts(List.of(helpResponse));
					return helpMsg;

				default:
					// Try to execute as a general command
					return client.getSessions().executeCommand(sessionId, cmd, args);
			}
		}
		catch (Exception e) {
			logger.error("Command execution failed", e);
			Message.TextPart errorPart = new Message.TextPart();
			errorPart.setText("Error executing command: " + e.getMessage());
			Message errorMsg = new Message();
			errorMsg.setParts(List.of(errorPart));
			return errorMsg;
		}

		// Return empty message if nothing was executed
		return new Message();
	}

}