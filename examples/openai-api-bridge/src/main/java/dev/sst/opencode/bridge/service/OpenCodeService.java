package dev.sst.opencode.bridge.service;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.PromptRequest;
import dev.sst.opencode.models.requests.*;
import dev.sst.opencode.bridge.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OpenCodeService {

	private static final Logger logger = LoggerFactory.getLogger(OpenCodeService.class);

	private OpenCodeClient client;

	private final Map<String, dev.sst.opencode.bridge.model.Session> sessions = new ConcurrentHashMap<>();

	private Process opencodeProcess;

	@Value("${opencode.server.url}")
	private String opencodeUrl;

	@Value("${opencode.server.api-key}")
	private String apiKey;

	@Value("${opencode.server.timeout}")
	private int timeout;

	@Value("${opencode.server.auto-start}")
	private boolean autoStart;

	@Value("${opencode.server.binary-path}")
	private String binaryPath;

	@PostConstruct
	public void init() {
		if (autoStart) {
			startOpenCodeServer();
		}

		client = OpenCodeClient.builder().baseUrl(opencodeUrl).apiKey(apiKey).build();

		logger.info("OpenCode service initialized with URL: {}", opencodeUrl);
	}

	@PreDestroy
	public void cleanup() {
		if (opencodeProcess != null && opencodeProcess.isAlive()) {
			logger.info("Stopping OpenCode server...");
			opencodeProcess.destroyForcibly();
		}
	}

	private void startOpenCodeServer() {
		try {
			logger.info("Starting OpenCode server with binary: {}", binaryPath);
			ProcessBuilder pb = new ProcessBuilder(binaryPath, "serve");
			pb.inheritIO();
			opencodeProcess = pb.start();

			// Wait a bit for server to start
			Thread.sleep(3000);
			logger.info("OpenCode server started");
		}
		catch (Exception e) {
			logger.warn("Failed to start OpenCode server: {}", e.getMessage());
		}
	}

	public Mono<ChatCompletionResponse> createChatCompletion(ChatCompletionRequest request) {
		return Mono.fromCallable(() -> {
			try {
				// Get or create session
				String sessionId = getOrCreateSession(request.getUser());
				dev.sst.opencode.bridge.model.Session session = sessions.get(sessionId);

				// Determine provider and model
				String providerId = mapModelToProvider(request.getModel());
				String modelId = mapModel(request.getModel());

				// Send prompt to OpenCode
				String prompt = combineMessagesFromOpenAI(request.getMessages());
				PromptRequest promptRequest = PromptRequest.ofText(prompt, providerId, modelId);

				// Get the initial message ID before sending
				List<Message> beforeMessages = client.getSessions().getMessages(sessionId);
				int beforeCount = beforeMessages != null ? beforeMessages.size() : 0;

				// Send the prompt
				Message finalResponse = client.getSessions().sendPrompt(sessionId, promptRequest);

				// Get all messages after the prompt to capture multi-turn actions
				List<Message> afterMessages = client.getSessions().getMessages(sessionId);

				// Extract all new messages (including multi-turn)
				List<Message> newMessages = afterMessages.stream().skip(beforeCount).collect(Collectors.toList());

				// Convert to OpenAI response with all actions included
				return convertToOpenAIResponseWithFullContext(newMessages, request);

			}
			catch (Exception e) {
				logger.error("Error processing chat completion", e);
				throw new RuntimeException("Failed to process chat completion", e);
			}
		});
	}

	public Flux<String> streamChatCompletion(ChatCompletionRequest request) {
		return Flux.create(sink -> {
			try {
				String sessionId = getOrCreateSession(request.getUser());

				// Get initial message count
				List<Message> beforeMessages = client.getSessions().getMessages(sessionId);
				int beforeCount = beforeMessages != null ? beforeMessages.size() : 0;

				String prompt = combineMessagesFromOpenAI(request.getMessages());
				PromptRequest promptRequest = PromptRequest.ofText(prompt, mapModelToProvider(request.getModel()),
						mapModel(request.getModel()));

				// Send prompt
				Message response = client.getSessions().sendPrompt(sessionId, promptRequest);

				// Get all messages after to capture multi-turn
				List<Message> afterMessages = client.getSessions().getMessages(sessionId);
				List<Message> newMessages = afterMessages.stream().skip(beforeCount).collect(Collectors.toList());

				// Stream each part of the multi-turn response
				streamMultiTurnResponse(newMessages, request, new StreamCallback() {
					private String currentId = "chatcmpl-" + System.nanoTime();

					@Override
					public void onNext(String chunk) {
						ChatCompletionChunk streamChunk = new ChatCompletionChunk();
						streamChunk.setId(currentId);
						streamChunk.setModel(request.getModel());

						ChatCompletionChunk.ChunkChoice choice = new ChatCompletionChunk.ChunkChoice();
						choice.setIndex(0);

						ChatCompletionChunk.Delta delta = new ChatCompletionChunk.Delta();
						delta.setContent(chunk);
						choice.setDelta(delta);

						streamChunk.setChoices(Collections.singletonList(choice));

						try {
							String json = objectMapper.writeValueAsString(streamChunk);
							sink.next("data: " + json + "\n\n");
						}
						catch (Exception e) {
							logger.error("Error serializing chunk", e);
						}
					}

					@Override
					public void onComplete() {
						// Send final chunk with finish reason
						ChatCompletionChunk finalChunk = new ChatCompletionChunk();
						finalChunk.setId(currentId);
						finalChunk.setModel(request.getModel());

						ChatCompletionChunk.ChunkChoice choice = new ChatCompletionChunk.ChunkChoice();
						choice.setIndex(0);
						choice.setFinishReason("stop");
						choice.setDelta(new ChatCompletionChunk.Delta());

						finalChunk.setChoices(Collections.singletonList(choice));

						try {
							String json = objectMapper.writeValueAsString(finalChunk);
							sink.next("data: " + json + "\n\n");
							sink.next("data: [DONE]\n\n");
							sink.complete();
						}
						catch (Exception e) {
							logger.error("Error sending final chunk", e);
							sink.error(e);
						}
					}

					@Override
					public void onError(Throwable error) {
						logger.error("Stream error", error);
						sink.error(error);
					}
				});

			}
			catch (Exception e) {
				logger.error("Error starting stream", e);
				sink.error(e);
			}
		});
	}

	private String getOrCreateSession(String user) {
		String sessionId = user != null ? user : "default";
		sessions.computeIfAbsent(sessionId, k -> {
			try {
				// Create a new session with OpenCode
				SessionCreateRequest createRequest = SessionCreateRequest.builder()
					.title("OpenAI Bridge Session")
					.build();

				dev.sst.opencode.models.Session opencodeSession = client.getSessions().createSession(createRequest);

				dev.sst.opencode.bridge.model.Session session = new dev.sst.opencode.bridge.model.Session();
				session.setId(opencodeSession.getId());
				session.setCreatedAt(System.currentTimeMillis());
				return session;
			}
			catch (Exception e) {
				logger.error("Failed to create session", e);
				// Fallback to a default session
				dev.sst.opencode.bridge.model.Session session = new dev.sst.opencode.bridge.model.Session();
				session.setId("session-" + UUID.randomUUID());
				session.setCreatedAt(System.currentTimeMillis());
				return session;
			}
		});
		return sessions.get(sessionId).getId();
	}

	private ChatCompletionResponse convertToOpenAIResponseWithFullContext(List<Message> messages,
			ChatCompletionRequest request) {
		ChatCompletionResponse response = new ChatCompletionResponse();
		response.setModel(request.getModel());

		// Format all multi-turn actions into a comprehensive response
		StringBuilder fullContent = new StringBuilder();

		for (Message msg : messages) {
			if (msg.getParts() != null) {
				for (Message.MessagePart part : msg.getParts()) {
					String formattedPart = formatMessagePart(part);
					if (formattedPart != null && !formattedPart.isEmpty()) {
						fullContent.append(formattedPart);
					}
				}
			}
		}

		ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
		choice.setIndex(0);
		choice.setFinishReason("stop");

		ChatCompletionRequest.ChatMessage message = new ChatCompletionRequest.ChatMessage();
		message.setRole("assistant");
		message.setContent(fullContent.toString());
		choice.setMessage(message);

		response.setChoices(Collections.singletonList(choice));

		// Estimate token usage
		ChatCompletionResponse.Usage usage = new ChatCompletionResponse.Usage();
		usage.setPromptTokens(estimateTokens(request.getMessages()));
		usage.setCompletionTokens(estimateTokens(fullContent.toString()));
		usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
		response.setUsage(usage);

		return response;
	}

	private String formatMessagePart(Message.MessagePart part) {
		if (part == null)
			return "";

		String type = part.getType();
		StringBuilder formatted = new StringBuilder();

		if (part instanceof Message.TextPart) {
			Message.TextPart textPart = (Message.TextPart) part;
			formatted.append(textPart.getText()).append("\n\n");

		}
		else if (part instanceof Message.ToolPart) {
			Message.ToolPart toolPart = (Message.ToolPart) part;
			formatted.append("\n### Tool Execution: ").append(toolPart.getTool()).append("\n");
			if (toolPart.getState() != null) {
				formatted.append("```\n");
				formatted.append(formatToolState(toolPart.getState()));
				formatted.append("```\n\n");
			}

		}
		else if (part instanceof Message.FilePart) {
			Message.FilePart filePart = (Message.FilePart) part;
			formatted.append("\n### File: ").append(filePart.getFilename()).append("\n");
			formatted.append("Type: ").append(filePart.getMime()).append("\n\n");

		}
		else if (part instanceof Message.SnapshotPart) {
			Message.SnapshotPart snapshotPart = (Message.SnapshotPart) part;
			formatted.append("\n### Code Snapshot\n");
			formatted.append("```\n");
			formatted.append(snapshotPart.getSnapshot());
			formatted.append("```\n\n");

		}
		else if (part instanceof Message.PatchPart) {
			Message.PatchPart patchPart = (Message.PatchPart) part;
			formatted.append("\n### Files Modified\n");
			if (patchPart.getFiles() != null) {
				for (String file : patchPart.getFiles()) {
					formatted.append("- ").append(file).append("\n");
				}
			}
			formatted.append("\n");
		}

		return formatted.toString();
	}

	private String formatToolState(Message.ToolState state) {
		if (state == null)
			return "";

		StringBuilder result = new StringBuilder();

		// Format based on tool state type
		if (state instanceof Message.ToolStatePending) {
			result.append("Status: Pending\n");

		}
		else if (state instanceof Message.ToolStateRunning) {
			Message.ToolStateRunning running = (Message.ToolStateRunning) state;
			result.append("Status: Running\n");
			if (running.getTitle() != null) {
				result.append("Title: ").append(running.getTitle()).append("\n");
			}
			if (running.getInput() != null) {
				result.append("Input: ").append(formatInput(running.getInput())).append("\n");
			}

		}
		else if (state instanceof Message.ToolStateCompleted) {
			Message.ToolStateCompleted completed = (Message.ToolStateCompleted) state;
			result.append("Status: Completed\n");
			if (completed.getTitle() != null) {
				result.append("Title: ").append(completed.getTitle()).append("\n");
			}
			if (completed.getInput() != null) {
				result.append("Input: ").append(formatInput(completed.getInput())).append("\n");
			}
			if (completed.getOutput() != null) {
				result.append("Output:\n").append(completed.getOutput()).append("\n");
			}

		}
		else if (state instanceof Message.ToolStateError) {
			Message.ToolStateError error = (Message.ToolStateError) state;
			result.append("Status: Error\n");
			if (error.getError() != null) {
				result.append("Error: ").append(error.getError()).append("\n");
			}
			if (error.getInput() != null) {
				result.append("Input: ").append(formatInput(error.getInput())).append("\n");
			}
		}

		return result.toString();
	}

	private String formatInput(Map<String, Object> input) {
		if (input == null)
			return "";

		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, Object> entry : input.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			// Format common input types
			if ("command".equals(key) || "cmd".equals(key)) {
				result.append("$ ").append(value).append("\n");
			}
			else if ("path".equals(key) || "file".equals(key)) {
				result.append("File: ").append(value).append("\n");
			}
			else if ("content".equals(key) && value instanceof String) {
				String content = (String) value;
				if (content.length() < 500) {
					result.append(content).append("\n");
				}
				else {
					result.append("[Content truncated: ").append(content.length()).append(" chars]\n");
				}
			}
			else {
				result.append(key).append(": ").append(value).append("\n");
			}
		}
		return result.toString();
	}

	private void streamMultiTurnResponse(List<Message> messages, ChatCompletionRequest request,
			StreamCallback callback) {
		try {
			for (Message msg : messages) {
				if (msg.getParts() != null) {
					for (Message.MessagePart part : msg.getParts()) {
						String formattedPart = formatMessagePart(part);
						if (formattedPart != null && !formattedPart.isEmpty()) {
							// Stream in chunks
							int chunkSize = 50;
							for (int i = 0; i < formattedPart.length(); i += chunkSize) {
								int end = Math.min(i + chunkSize, formattedPart.length());
								String chunk = formattedPart.substring(i, end);
								callback.onNext(chunk);
								Thread.sleep(20); // Small delay for streaming effect
							}
						}
					}
				}
			}
			callback.onComplete();
		}
		catch (Exception e) {
			callback.onError(e);
		}
	}

	private String combineMessagesFromOpenAI(List<ChatCompletionRequest.ChatMessage> messages) {
		// Combine OpenAI messages into a single prompt string
		StringBuilder prompt = new StringBuilder();
		for (ChatCompletionRequest.ChatMessage msg : messages) {
			if (msg != null && msg.getContent() != null) {
				prompt.append("[").append(msg.getRole()).append("]: ");
				prompt.append(msg.getContent()).append("\n");
			}
		}
		return prompt.toString();
	}

	private String mapModelToProvider(String model) {
		// Support provider/model format (e.g., "opencode/grok-code" or
		// "anthropic/claude-3")
		if (model.contains("/")) {
			String[] parts = model.split("/", 2);
			return parts[0]; // Return the provider part
		}

		// Default provider for models without explicit provider
		return "opencode";
	}

	private String mapModel(String model) {
		// Support provider/model format (e.g., "opencode/grok-code" or
		// "anthropic/claude-3")
		if (model.contains("/")) {
			String[] parts = model.split("/", 2);
			return parts[1]; // Return the model part
		}

		// Optional: Map common OpenAI model names for convenience
		// You can comment out this mapping to pass all models through as-is
		Map<String, String> modelMap = Map.of("gpt-4", "grok-code", "gpt-4-turbo", "grok-code", "gpt-3.5-turbo",
				"qwen3-coder", "gpt-3.5-turbo-16k", "qwen3-coder");

		// If it's a known OpenAI model, map it; otherwise pass through as-is
		return modelMap.getOrDefault(model, model);
	}

	private int estimateTokens(List<ChatCompletionRequest.ChatMessage> messages) {
		return messages.stream().mapToInt(msg -> estimateTokens(msg.getContent())).sum();
	}

	private int estimateTokens(String text) {
		if (text == null)
			return 0;
		// Rough estimation: ~4 characters per token
		return text.length() / 4;
	}

	private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

	public interface StreamCallback {

		void onNext(String chunk);

		void onComplete();

		void onError(Throwable error);

	}

}