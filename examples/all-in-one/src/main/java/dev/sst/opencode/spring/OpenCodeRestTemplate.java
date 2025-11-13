package dev.sst.opencode.spring;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.config.OpenCodeConfig;
import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.*;
import dev.sst.opencode.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Spring RestTemplate wrapper for OpenCode API Provides a familiar Spring-like interface
 * for OpenCode operations
 *
 * This class wraps the OpenCodeClient to provide Spring-specific conveniences while
 * delegating actual HTTP operations to the client.
 */
@Slf4j
public class OpenCodeRestTemplate {

	private final OpenCodeClient client;

	private final OpenCodeConfig config;

	// For backward compatibility, expose services
	private final SessionService sessionService;

	private final FileService fileService;

	private final ConfigService configService;

	private final EventService eventService;

	private final AppService appService;

	private final CommandService commandService;

	private final ToolService toolService;

	private final TuiService tuiService;

	private final LogService logService;

	private final ProjectService projectService;

	public OpenCodeRestTemplate(OpenCodeConfig config) {
		this.config = config;
		this.client = new OpenCodeClient(config);

		// Expose services from the client
		this.sessionService = client.getSessions();
		this.fileService = client.getFiles();
		this.configService = client.getConfiguration();
		this.eventService = client.getEvents();
		this.appService = client.getApp();
		this.commandService = client.getCommands();
		this.toolService = client.getTools();
		this.tuiService = client.getTui();
		this.logService = client.getLogs();
		this.projectService = client.getProjects();
	}

	/**
	 * Get the underlying OpenCodeClient
	 */
	public OpenCodeClient getClient() {
		return client;
	}

	/**
	 * Get session service
	 */
	public SessionService getSessionService() {
		return sessionService;
	}

	/**
	 * Get file service
	 */
	public FileService getFileService() {
		return fileService;
	}

	/**
	 * Get config service
	 */
	public ConfigService getConfigService() {
		return configService;
	}

	/**
	 * Get event service
	 */
	public EventService getEventService() {
		return eventService;
	}

	/**
	 * Get app service
	 */
	public AppService getAppService() {
		return appService;
	}

	/**
	 * Get command service
	 */
	public CommandService getCommandService() {
		return commandService;
	}

	/**
	 * Get tool service
	 */
	public ToolService getToolService() {
		return toolService;
	}

	/**
	 * Get TUI service
	 */
	public TuiService getTuiService() {
		return tuiService;
	}

	/**
	 * Get log service
	 */
	public LogService getLogService() {
		return logService;
	}

	/**
	 * Get project service
	 */
	public ProjectService getProjectService() {
		return projectService;
	}

	// ==================== Session Operations ====================

	/**
	 * Create a new session
	 */
	public Session createSession() {
		return createSession(null, null);
	}

	public Session createSession(String title) {
		return createSession(title, null);
	}

	public Session createSession(String title, String parentId) {
		SessionCreateRequest request = SessionCreateRequest.builder().title(title).parentId(parentId).build();

		return sessionService.createSession(request);
	}

	/**
	 * List all sessions
	 */
	public List<Session> listSessions() {
		return sessionService.listSessions();
	}

	/**
	 * Get a specific session
	 */
	public Session getSession(String sessionId) {
		return sessionService.getSession(sessionId);
	}

	/**
	 * Delete a session
	 */
	public void deleteSession(String sessionId) {
		sessionService.deleteSession(sessionId);
	}

	/**
	 * Update session title
	 */
	public Session updateSession(String sessionId, String newTitle) {
		return sessionService.updateSession(sessionId, newTitle);
	}

	// ==================== Message Operations ====================

	/**
	 * Send a prompt to a session
	 */
	public Message sendPrompt(String sessionId, String text, String providerId, String modelId) {
		PromptRequest request = PromptRequest.ofText(text, providerId, modelId);
		return sendPrompt(sessionId, request);
	}

	public Message sendPrompt(String sessionId, PromptRequest request) {
		return sessionService.sendPrompt(sessionId, request);
	}

	/**
	 * Send a prompt and wait for completion
	 */
	public CompletableFuture<Message> sendPromptAsync(String sessionId, String text, String providerId,
			String modelId) {
		return CompletableFuture.supplyAsync(() -> sendPrompt(sessionId, text, providerId, modelId));
	}

	/**
	 * Get all messages for a session
	 */
	public List<Message> getMessages(String sessionId) {
		return sessionService.getMessages(sessionId);
	}

	/**
	 * Get a specific message
	 */
	public Message getMessage(String sessionId, String messageId) {
		return sessionService.getMessage(sessionId, messageId);
	}

	// ==================== Command Operations ====================

	/**
	 * Execute a command
	 */
	public Message executeCommand(String sessionId, String command, String arguments) {
		return sessionService.executeCommand(sessionId, command, arguments);
	}

	// ==================== File Operations ====================

	/**
	 * Read a file
	 */
	public FileContent readFile(String path) {
		return fileService.readFile(path);
	}

	/**
	 * List files in a directory
	 */
	public List<FileNode> listFiles(String path) {
		return fileService.listFiles(path);
	}

	/**
	 * Search for text in files
	 */
	public List<SearchMatch> searchText(String pattern) {
		return fileService.searchText(pattern);
	}

	/**
	 * Find files by name
	 */
	public List<String> findFiles(String query) {
		return fileService.findFiles(query);
	}

	// ==================== Event Streaming (SSE) ====================

	/**
	 * Subscribe to server events using Server-Sent Events Returns a reactive Flux that
	 * emits events as they arrive
	 */
	public Flux<OpenCodeEvent> subscribeToEvents() {
		return eventService.subscribeToEvents();
	}

	/**
	 * Subscribe to events with a callback (non-reactive alternative)
	 */
	public void subscribeToEvents(EventCallback callback) {
		eventService.subscribeWithCallback(callback::onEvent, callback::onError, callback::onComplete);
	}

	// ==================== Configuration ====================

	/**
	 * Get configuration info
	 */
	public ConfigInfo getConfig() {
		return configService.getConfig();
	}

	/**
	 * List available providers
	 */
	public ProvidersResponse listProviders() {
		return configService.listProviders();
	}

	// ==================== Project Operations ====================

	/**
	 * List all projects
	 */
	public List<Project> listProjects() {
		return projectService.listProjects();
	}

	/**
	 * Get the current project
	 */
	public Project getCurrentProject() {
		return projectService.getCurrentProject();
	}

	// ==================== App Operations ====================

	// ==================== Command/Agent Operations ====================

	/**
	 * List all available commands
	 */
	public List<Command> listCommands() {
		return commandService.listCommands();
	}

	/**
	 * List all available agents
	 */
	public List<Agent> listAgents() {
		return commandService.listAgents();
	}

	// ==================== Tool Operations ====================

	/**
	 * Register a new HTTP callback tool
	 */
	public Tool registerTool(ToolRegisterRequest request) {
		return toolService.registerTool(request);
	}

	/**
	 * List all tool IDs
	 */
	public List<String> listToolIds() {
		return toolService.listToolIds();
	}

	// ==================== TUI Operations ====================

	/**
	 * Show a toast notification in the TUI
	 */
	public void showToast(String message) {
		tuiService.showToast(message, "info", null);
	}

	public void showToast(String message, String type) {
		tuiService.showToast(message, type, null);
	}

	// ==================== Logging Operations ====================

	/**
	 * Log a message to the server
	 */
	public void logInfo(String message) {
		logService.info(message, "JavaSDK");
	}

	public void logError(String message) {
		logService.error(message, "JavaSDK");
	}

	public void logWarn(String message) {
		logService.warn(message, "JavaSDK");
	}

	public void logDebug(String message) {
		logService.debug(message, "JavaSDK");
	}

	// ==================== Extended Session Operations ====================

	/**
	 * Get session children
	 */
	public List<Session> getSessionChildren(String sessionId) {
		return sessionService.getSessionChildren(sessionId);
	}

	/**
	 * Generate session summary
	 */
	public SessionSummary summarizeSession(String sessionId, String providerId, String modelId) {
		return sessionService.summarizeSession(sessionId, providerId, modelId);
	}

	/**
	 * Execute shell command in session
	 */
	public Message executeShellCommand(String sessionId, String command) {
		ShellRequest request = ShellRequest.builder().command(command).build();
		return sessionService.executeShellCommand(sessionId, request);
	}

	/**
	 * Respond to permission request
	 */
	public void grantPermission(String sessionId, String permissionId) {
		PermissionResponse response = PermissionResponse.builder().granted(true).build();
		sessionService.respondToPermission(sessionId, permissionId, response);
	}

	public void denyPermission(String sessionId, String permissionId, String reason) {
		PermissionResponse response = PermissionResponse.builder().granted(false).reason(reason).build();
		sessionService.respondToPermission(sessionId, permissionId, response);
	}

	// ==================== Helper Classes ====================

	@FunctionalInterface
	public interface EventCallback {

		void onEvent(OpenCodeEvent event);

		default void onError(Throwable error) {
			log.error("Event stream error", error);
		}

		default void onComplete() {
			log.info("Event stream completed");
		}

	}

	// ==================== Builder ====================

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String baseUrl = "http://localhost:8080";

		private String apiKey;

		private String workingDirectory;

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workingDirectory(String workingDirectory) {
			this.workingDirectory = workingDirectory;
			return this;
		}

		public OpenCodeRestTemplate build() {
			OpenCodeConfig config = OpenCodeConfig.builder()
				.baseUrl(baseUrl)
				.apiKey(apiKey)
				.workingDirectory(workingDirectory)
				.build();

			return new OpenCodeRestTemplate(config);
		}

	}

}