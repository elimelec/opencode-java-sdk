package dev.sst.opencode.examples.chat.controller;

import dev.sst.opencode.examples.chat.model.SimpleChatMessage;
import dev.sst.opencode.examples.chat.model.SimpleChatResponse;
import dev.sst.opencode.examples.chat.service.OpenCodeService;
import dev.sst.opencode.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SimpleChatController {

	private static final Logger logger = LoggerFactory.getLogger(SimpleChatController.class);

	@Autowired
	private OpenCodeService openCodeService;

	private String currentSessionId = null;

	private String currentProviderId = "opencode";

	private String currentModelId = "grok-code";

	@PostMapping("/server/start")
	public Map<String, Object> startServer() {
		boolean success = openCodeService.startOpenCodeServer();
		Map<String, Object> response = new HashMap<>();
		response.put("success", success);
		response.put("running", success);
		response.put("url", openCodeService.getServerUrl());

		if (success) {
			logger.info("OpenCode server started successfully");
		}
		else {
			logger.error("Failed to start OpenCode server");
		}

		return response;
	}

	@PostMapping("/server/stop")
	public Map<String, Object> stopServer() {
		boolean success = openCodeService.stopOpenCodeServer();
		Map<String, Object> response = new HashMap<>();
		response.put("success", success);
		response.put("running", false);
		logger.info("OpenCode server stop requested");
		return response;
	}

	@GetMapping("/server/status")
	public Map<String, Object> serverStatus() {
		boolean running = openCodeService.isServerRunning();
		Map<String, Object> response = new HashMap<>();
		response.put("running", running);
		response.put("url", openCodeService.getServerUrl());
		return response;
	}

	@PostMapping("/session/new")
	public Map<String, Object> newSession() {
		try {
			currentSessionId = openCodeService.createSession("Chat Session " + System.currentTimeMillis());
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("sessionId", currentSessionId);
			logger.info("New session created: {}", currentSessionId);
			return response;
		}
		catch (Exception e) {
			logger.error("Failed to create session", e);
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("error", e.getMessage());
			return response;
		}
	}

	@GetMapping("/providers")
	public Map<String, Object> getProviders() {
		return openCodeService.getProviders();
	}

	@GetMapping("/models/{providerId}")
	public Map<String, Object> getModels(@PathVariable String providerId) {
		currentProviderId = providerId;
		return openCodeService.getModels(providerId);
	}

	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public SimpleChatResponse handleWebSocketMessage(SimpleChatMessage message) {
		logger.info("WebSocket message received: {}", message.getContent());
		return processMessage(message);
	}

	@PostMapping("/chat/send")
	public SimpleChatResponse sendMessage(@RequestBody SimpleChatMessage message) {
		logger.info("REST message received: {}", message.getContent());
		return processMessage(message);
	}

	private SimpleChatResponse processMessage(SimpleChatMessage message) {
		SimpleChatResponse response = new SimpleChatResponse();

		try {
			// Create session if needed
			if (currentSessionId == null) {
				currentSessionId = openCodeService.createSession("Chat Session");
			}

			// Store provider and model from message if provided
			if (message.getProviderId() != null) {
				currentProviderId = message.getProviderId();
			}
			if (message.getModelId() != null) {
				currentModelId = message.getModelId();
			}

			// Send message to OpenCode
			Message opencodeResponse = openCodeService.sendMessage(currentSessionId, message.getContent(),
					currentProviderId, currentModelId);

			// Format the response
			String formattedResponse = openCodeService.formatMessageResponse(opencodeResponse);

			response.setContent(formattedResponse);
			response.setType("ASSISTANT");
			response.setSessionId(currentSessionId);
			response.setSuccess(true);

			// Check for /new command
			if (message.getContent().startsWith("/new")) {
				currentSessionId = null; // Reset for next message
			}

		}
		catch (Exception e) {
			logger.error("Error processing message", e);
			response.setContent("Error: " + e.getMessage());
			response.setType("ERROR");
			response.setSessionId(currentSessionId);
			response.setSuccess(false);
		}

		return response;
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		boolean serverRunning = openCodeService.isServerRunning();
		return Map.of("status", serverRunning ? "UP" : "DOWN", "service", "OpenCode Chat Example", "opencode",
				serverRunning ? "connected" : "disconnected");
	}

}