package dev.sst.opencode.client;

import dev.sst.opencode.config.OpenCodeConfig;
import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.*;
import dev.sst.opencode.services.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.sst.opencode.exceptions.OpenCodeException;
import dev.sst.opencode.utils.JsonUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Main OpenCode client for Java applications
 *
 * Example usage: <pre>
 * OpenCodeClient client = OpenCodeClient.builder()
 *     .baseUrl("http://localhost:8080")
 *     .build();
 *
 * Session session = client.sessions().create("My Session");
 * Message response = client.sessions().sendPrompt(
 *     session.getId(),
 *     "Write hello world in Python",
 *     "anthropic",
 *     "claude-3-5-sonnet-latest"
 * );
 * </pre>
 */
@Slf4j
@Getter
public class OpenCodeClient {

	private final OpenCodeConfig config;

	private final OkHttpClient httpClient;

	// Services
	private final SessionService sessions;

	private final FileService files;

	private final ConfigService configuration;

	private final EventService events;

	private final AppService app;

	private final CommandService commands;

	private final ToolService tools;

	private final TuiService tui;

	private final LogService logs;

	private final ProjectService projects;

	public OpenCodeClient() {
		this(OpenCodeConfig.fromEnvironment());
	}

	public OpenCodeClient(OpenCodeConfig config) {
		this.config = config;

		// Build HTTP client
		this.httpClient = new OkHttpClient.Builder().connectTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
			.readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
			.writeTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
			.addInterceptor(chain -> {
				Request original = chain.request();
				Request.Builder builder = original.newBuilder()
					.header("Accept", "application/json")
					.header("Content-Type", "application/json");

				// Add auth header if configured
				if (config.getApiKey() != null) {
					builder.header("Authorization", "Bearer " + config.getApiKey());
				}

				// Add directory query param if configured
				if (config.getWorkingDirectory() != null) {
					String url = original.url().toString();
					String separator = url.contains("?") ? "&" : "?";
					builder.url(url + separator + "directory=" + config.getWorkingDirectory());
				}

				Request request = builder.build();
				log.debug("Request: {} {}", request.method(), request.url());

				Response response = chain.proceed(request);
				log.debug("Response: {} - {}", response.code(), response.message());

				return response;
			})
			.build();

		// Initialize services
		this.sessions = new SessionServiceImpl(this);
		this.files = new FileServiceImpl(this);
		this.configuration = new ConfigServiceImpl(this);
		this.events = new EventServiceImpl(this);
		this.app = new AppServiceImpl(this);
		this.commands = new CommandServiceImpl(this);
		this.tools = new ToolServiceImpl(this);
		this.tui = new TuiServiceImpl(this);
		this.logs = new LogServiceImpl(this);
		this.projects = new ProjectServiceImpl(this);
	}

	/**
	 * Get the base URL
	 */
	public String getBaseUrl() {
		return config.getBaseUrl();
	}

	/**
	 * Execute a raw HTTP request
	 */
	public Response execute(Request request) throws IOException {
		return httpClient.newCall(request).execute();
	}

	/**
	 * Create an SSE event source
	 */
	public EventSource createEventSource(String path, EventSourceListener listener) {
		Request request = new Request.Builder().url(config.getBaseUrl() + path)
			.header("Accept", "text/event-stream")
			.build();

		return EventSources.createFactory(httpClient).newEventSource(request, listener);
	}

	/**
	 * Close the client and release resources
	 */
	public void close() {
		httpClient.dispatcher().executorService().shutdown();
		httpClient.connectionPool().evictAll();
		if (httpClient.cache() != null) {
			try {
				httpClient.cache().close();
			}
			catch (IOException e) {
				log.error("Error closing cache", e);
			}
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

		private int timeout = 30000;

		private int maxRetries = 3;

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

		public Builder timeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder maxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
			return this;
		}

		public OpenCodeClient build() {
			OpenCodeConfig config = OpenCodeConfig.builder()
				.baseUrl(baseUrl)
				.apiKey(apiKey)
				.workingDirectory(workingDirectory)
				.timeout(timeout)
				.maxRetries(maxRetries)
				.build();

			return new OpenCodeClient(config);
		}

	}

	/**
	 * Helper method to execute HTTP requests
	 */
	private <T> T executeRequest(Request request, Class<T> responseType) {
		try (Response response = httpClient.newCall(request).execute()) {
			String body = response.body() != null ? response.body().string() : null;

			if (!response.isSuccessful()) {
				handleErrorResponse(response, body);
			}

			if (responseType == Void.class || body == null || body.isEmpty()) {
				return null;
			}

			return JsonUtils.fromJson(body, responseType);
		}
		catch (IOException e) {
			throw new OpenCodeException.NetworkError("Network error during request", e);
		}
	}

	/**
	 * Helper method to execute HTTP requests with TypeReference
	 */
	private <T> T executeRequest(Request request, TypeReference<T> typeRef) {
		try (Response response = httpClient.newCall(request).execute()) {
			String body = response.body() != null ? response.body().string() : null;

			if (!response.isSuccessful()) {
				handleErrorResponse(response, body);
			}

			if (body == null || body.isEmpty()) {
				return null;
			}

			return JsonUtils.fromJson(body, typeRef);
		}
		catch (IOException e) {
			throw new OpenCodeException.NetworkError("Network error during request", e);
		}
	}

	private void handleErrorResponse(Response response, String body) {
		String message = body != null ? body : "HTTP " + response.code();

		switch (response.code()) {
			case 400:
				throw new OpenCodeException.BadRequest(message);
			case 401:
				throw new OpenCodeException.Unauthorized(message);
			case 404:
				throw new OpenCodeException.NotFound(message);
			case 500:
			case 502:
			case 503:
				throw new OpenCodeException.ServerError(message);
			default:
				throw new OpenCodeException(message, response.code(), null);
		}
	}

	private RequestBody createJsonBody(Object obj) {
		String json = JsonUtils.toJson(obj);
		return RequestBody.create(json, MediaType.parse("application/json"));
	}

	// Service implementations
	private class SessionServiceImpl implements SessionService {

		private final OpenCodeClient client;

		SessionServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		// Implementation of SessionService methods...
		// These would make HTTP calls using the client's httpClient

		@Override
		public Session createSession(SessionCreateRequest request) {
			Request httpRequest = new Request.Builder().url(config.getBaseUrl() + "/session")
				.post(createJsonBody(request != null ? request : new SessionCreateRequest()))
				.build();

			return executeRequest(httpRequest, Session.class);
		}

		@Override
		public List<Session> listSessions() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session").get().build();

			return executeRequest(request, new TypeReference<List<Session>>() {
			});
		}

		@Override
		public Session getSession(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId).get().build();

			return executeRequest(request, Session.class);
		}

		@Override
		public Session updateSession(String sessionId, String title) {
			Map<String, String> body = Map.of("title", title);
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId)
				.patch(createJsonBody(body))
				.build();

			return executeRequest(request, Session.class);
		}

		@Override
		public void deleteSession(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId).delete().build();

			executeRequest(request, Void.class);
		}

		@Override
		public Message sendPrompt(String sessionId, PromptRequest promptRequest) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/message")
				.post(createJsonBody(promptRequest))
				.build();

			return executeRequest(request, Message.class);
		}

		@Override
		public CompletableFuture<Message> sendPromptAsync(String sessionId, PromptRequest request) {
			return CompletableFuture.supplyAsync(() -> sendPrompt(sessionId, request));
		}

		@Override
		public List<Message> getMessages(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/message")
				.get()
				.build();

			return executeRequest(request, new TypeReference<List<Message>>() {
			});
		}

		@Override
		public Message getMessage(String sessionId, String messageId) {
			Request request = new Request.Builder()
				.url(config.getBaseUrl() + "/session/" + sessionId + "/message/" + messageId)
				.get()
				.build();

			return executeRequest(request, Message.class);
		}

		@Override
		public Message executeCommand(String sessionId, String command, String arguments) {
			Map<String, Object> body = new HashMap<>();
			body.put("command", command);
			body.put("arguments", arguments != null ? arguments : "");
			// Add agent field if command starts with slash (built-in command)
			if (command != null && command.startsWith("/")) {
				body.put("agent", "shell");
			}

			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/command")
				.post(createJsonBody(body))
				.build();

			return executeRequest(request, Message.class);
		}

		@Override
		public Session shareSession(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/share")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			return executeRequest(request, Session.class);
		}

		@Override
		public Session unshareSession(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/share")
				.delete()
				.build();

			return executeRequest(request, Session.class);
		}

		@Override
		public void initializeSession(String sessionId, String providerId, String modelId) {
			Map<String, String> body = Map.of("messageID", "init_" + System.currentTimeMillis(), "providerID",
					providerId, "modelID", modelId);
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/init")
				.post(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void abortSession(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/abort")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public Session revertMessage(String sessionId, String messageId, String partId) {
			Map<String, String> body = Map.of("messageID", messageId, "partID", partId != null ? partId : "");
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/revert")
				.post(createJsonBody(body))
				.build();

			return executeRequest(request, Session.class);
		}

		@Override
		public Session unrevertMessages(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/unrevert")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			return executeRequest(request, Session.class);
		}

		@Override
		public List<Session> getSessionChildren(String sessionId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/children")
				.get()
				.build();

			return executeRequest(request, new TypeReference<List<Session>>() {
			});
		}

		@Override
		public SessionSummary summarizeSession(String sessionId, String providerId, String modelId) {
			Map<String, String> body = Map.of("providerID", providerId, "modelID", modelId);
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/summarize")
				.post(createJsonBody(body))
				.build();

			return executeRequest(request, SessionSummary.class);
		}

		@Override
		public Message executeShellCommand(String sessionId, ShellRequest shellRequest) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/session/" + sessionId + "/shell")
				.post(createJsonBody(shellRequest))
				.build();

			return executeRequest(request, Message.class);
		}

		@Override
		public void respondToPermission(String sessionId, String permissionId, PermissionResponse response) {
			Request request = new Request.Builder()
				.url(config.getBaseUrl() + "/session/" + sessionId + "/permissions/" + permissionId)
				.post(createJsonBody(response))
				.build();

			executeRequest(request, Void.class);
		}

	}

	private class FileServiceImpl implements FileService {

		private final OpenCodeClient client;

		FileServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		// Implementation of FileService methods...
		@Override
		public FileContent readFile(String path) {
			try {
				String encodedPath = java.net.URLEncoder.encode(path, "UTF-8");
				Request request = new Request.Builder().url(config.getBaseUrl() + "/file?path=" + encodedPath)
					.get()
					.build();

				return executeRequest(request, FileContent.class);
			}
			catch (java.io.UnsupportedEncodingException e) {
				throw new OpenCodeException("Failed to encode file path", e);
			}
		}

		@Override
		public List<FileNode> listFiles(String path) {
			try {
				String encodedPath = java.net.URLEncoder.encode(path, "UTF-8");
				Request request = new Request.Builder().url(config.getBaseUrl() + "/file?path=" + encodedPath)
					.get()
					.build();

				return executeRequest(request, new TypeReference<List<FileNode>>() {
				});
			}
			catch (java.io.UnsupportedEncodingException e) {
				throw new OpenCodeException("Failed to encode file path", e);
			}
		}

		@Override
		public List<FileNode> getFileStatus() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/file/status").get().build();

			return executeRequest(request, new TypeReference<List<FileNode>>() {
			});
		}

		@Override
		public List<SearchMatch> searchText(String pattern) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/find?pattern=" + pattern).get().build();

			return executeRequest(request, new TypeReference<List<SearchMatch>>() {
			});
		}

		@Override
		public List<String> findFiles(String query) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/find/file?query=" + query)
				.get()
				.build();

			return executeRequest(request, new TypeReference<List<String>>() {
			});
		}

		@Override
		public List<Object> findSymbols(String query) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/find/symbol?query=" + query)
				.get()
				.build();

			return executeRequest(request, new TypeReference<List<Object>>() {
			});
		}

	}

	private class ConfigServiceImpl implements ConfigService {

		private final OpenCodeClient client;

		ConfigServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		// Implementation of ConfigService methods...
		@Override
		public ConfigInfo getConfig() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/config").get().build();

			return executeRequest(request, ConfigInfo.class);
		}

		@Override
		public ProvidersResponse listProviders() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/config/providers").get().build();

			return executeRequest(request, ProvidersResponse.class);
		}

		@Override
		public String getWorkingDirectory() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/path").get().build();

			Map<String, String> response = executeRequest(request, new TypeReference<Map<String, String>>() {
			});
			return response != null ? response.get("directory") : null;
		}

		@Override
		public void setAuthentication(String providerId, String credentials) {
			Map<String, String> body = Map.of("credentials", credentials);
			Request request = new Request.Builder().url(config.getBaseUrl() + "/auth/" + providerId)
				.put(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

	}

	private class EventServiceImpl implements EventService {

		private final OpenCodeClient client;

		EventServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		private EventSource eventSource;

		@Override
		public reactor.core.publisher.Flux<OpenCodeEvent> subscribeToEvents() {
			return reactor.core.publisher.Flux.create(sink -> {
				EventSourceListener listener = new EventSourceListener() {
					@Override
					public void onEvent(EventSource eventSource, String id, String type, String data) {
						try {
							OpenCodeEvent event = JsonUtils.fromJson(data, OpenCodeEvent.class);
							sink.next(event);
						}
						catch (Exception e) {
							sink.error(e);
						}
					}

					@Override
					public void onFailure(EventSource eventSource, Throwable t, Response response) {
						sink.error(new OpenCodeException("SSE connection failed", t));
					}

					@Override
					public void onClosed(EventSource eventSource) {
						sink.complete();
					}
				};

				Request request = new Request.Builder().url(config.getBaseUrl() + "/event")
					.header("Accept", "text/event-stream")
					.build();

				eventSource = createEventSource(request.url().toString(), listener);

				sink.onDispose(() -> {
					if (eventSource != null) {
						eventSource.cancel();
					}
				});
			});
		}

		@Override
		public void subscribeWithCallback(java.util.function.Consumer<OpenCodeEvent> onEvent,
				java.util.function.Consumer<Throwable> onError, Runnable onComplete) {
			subscribeToEvents().subscribe(onEvent::accept, onError::accept, onComplete::run);
		}

		@Override
		public reactor.core.publisher.Flux<OpenCodeEvent> subscribeToEvents(String eventTypeFilter) {
			return subscribeToEvents()
				.filter(event -> event.getType() != null && event.getType().contains(eventTypeFilter));
		}

		@Override
		public void closeEventStream() {
			if (eventSource != null) {
				eventSource.cancel();
				eventSource = null;
			}
		}

	}

	private class AppServiceImpl implements AppService {

		private final OpenCodeClient client;

		AppServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public String getOpenApiDoc() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/doc").get().build();

			try (Response response = httpClient.newCall(request).execute()) {
				if (response.isSuccessful() && response.body() != null) {
					return response.body().string();
				}
				throw new OpenCodeException("Failed to get OpenAPI documentation", response.code(), null);
			}
			catch (IOException e) {
				throw new OpenCodeException.NetworkError("Failed to get OpenAPI documentation", e);
			}
		}

	}

	private class CommandServiceImpl implements CommandService {

		private final OpenCodeClient client;

		CommandServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public List<Command> listCommands() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/command").get().build();

			return executeRequest(request, new TypeReference<List<Command>>() {
			});
		}

		@Override
		public Command getCommand(String name) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/command/" + name).get().build();

			return executeRequest(request, Command.class);
		}

		@Override
		public List<Agent> listAgents() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/agent").get().build();

			return executeRequest(request, new TypeReference<List<Agent>>() {
			});
		}

		@Override
		public Agent getAgent(String agentId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/agent/" + agentId).get().build();

			return executeRequest(request, Agent.class);
		}

	}

	private class ToolServiceImpl implements ToolService {

		private final OpenCodeClient client;

		ToolServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public Tool registerTool(ToolRegisterRequest request) {
			Request httpRequest = new Request.Builder().url(config.getBaseUrl() + "/experimental/tool/register")
				.post(createJsonBody(request))
				.build();

			return executeRequest(httpRequest, Tool.class);
		}

		@Override
		public List<String> listToolIds() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/experimental/tool/ids").get().build();

			return executeRequest(request, new TypeReference<List<String>>() {
			});
		}

		@Override
		public List<Tool> listTools(String providerId, String modelId) {
			String url = config.getBaseUrl() + "/experimental/tool";
			// Only add query params if both are provided
			if (providerId != null && modelId != null) {
				url += "?provider=" + providerId + "&model=" + modelId;
			}
			else if (providerId != null || modelId != null) {
				// If only one is provided, it's likely an error
				throw new IllegalArgumentException("Both provider and model must be specified together or both null");
			}

			Request request = new Request.Builder().url(url).get().build();

			return executeRequest(request, new TypeReference<List<Tool>>() {
			});
		}

		@Override
		public Tool getTool(String toolId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/experimental/tool/" + toolId)
				.get()
				.build();

			return executeRequest(request, Tool.class);
		}

		@Override
		public void unregisterTool(String toolId) {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/experimental/tool/" + toolId)
				.delete()
				.build();

			executeRequest(request, Void.class);
		}

	}

	private class TuiServiceImpl implements TuiService {

		private final OpenCodeClient client;

		TuiServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public void appendPrompt(String text) {
			TuiRequest body = TuiRequest.builder().text(text).build();
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/append-prompt")
				.post(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void submitPrompt() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/submit-prompt")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void clearPrompt() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/clear-prompt")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void openHelp() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/open-help")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void openSessions() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/open-sessions")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void openThemes() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/open-themes")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void openModels() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/open-models")
				.post(RequestBody.create("{}", MediaType.parse("application/json")))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void executeCommand(String command) {
			TuiRequest body = TuiRequest.builder().command(command).build();
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/execute-command")
				.post(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public void showToast(String message, String type, Integer duration) {
			TuiRequest body = TuiRequest.builder().message(message).variant(type).duration(duration).build();
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/show-toast")
				.post(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

		@Override
		public Object getNextControlRequest() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/control/next").get().build();

			return executeRequest(request, Object.class);
		}

		@Override
		public void submitControlResponse(Object response) {
			TuiRequest body = TuiRequest.builder().response(response).build();
			Request request = new Request.Builder().url(config.getBaseUrl() + "/tui/control/response")
				.post(createJsonBody(body))
				.build();

			executeRequest(request, Void.class);
		}

	}

	private class LogServiceImpl implements LogService {

		private final OpenCodeClient client;

		LogServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public void log(LogRequest request) {
			Request httpRequest = new Request.Builder().url(config.getBaseUrl() + "/log")
				.post(createJsonBody(request))
				.build();

			executeRequest(httpRequest, Void.class);
		}

		@Override
		public void debug(String message, String source) {
			log(LogRequest.builder()
				.level("debug")
				.message(message)
				.service(source)
				.timestamp(System.currentTimeMillis())
				.build());
		}

		@Override
		public void info(String message, String source) {
			log(LogRequest.builder()
				.level("info")
				.message(message)
				.service(source)
				.timestamp(System.currentTimeMillis())
				.build());
		}

		@Override
		public void warn(String message, String source) {
			log(LogRequest.builder()
				.level("warn")
				.message(message)
				.service(source)
				.timestamp(System.currentTimeMillis())
				.build());
		}

		@Override
		public void error(String message, String source) {
			log(LogRequest.builder()
				.level("error")
				.message(message)
				.service(source)
				.timestamp(System.currentTimeMillis())
				.build());
		}

	}

	private class ProjectServiceImpl implements ProjectService {

		private final OpenCodeClient client;

		ProjectServiceImpl(OpenCodeClient client) {
			this.client = client;
		}

		@Override
		public List<Project> listProjects() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/project").get().build();

			return executeRequest(request, new TypeReference<List<Project>>() {
			});
		}

		@Override
		public Project getCurrentProject() {
			Request request = new Request.Builder().url(config.getBaseUrl() + "/project/current").get().build();

			return executeRequest(request, Project.class);
		}

	}

}