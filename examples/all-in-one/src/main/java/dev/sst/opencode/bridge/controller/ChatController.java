package dev.sst.opencode.bridge.controller;

import dev.sst.opencode.bridge.model.ChatCompletionRequest;
import dev.sst.opencode.bridge.model.ChatCompletionResponse;
import dev.sst.opencode.bridge.service.OpenCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Chat Completions", description = "OpenAI-compatible chat completion endpoints")
@CrossOrigin(origins = "*")
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	private OpenCodeService openCodeService;

	@PostMapping("/chat/completions")
	@Operation(summary = "Create chat completion",
			description = "Creates a model response for the given chat conversation. Compatible with OpenAI API.")
	public Object createChatCompletion(@Valid @RequestBody ChatCompletionRequest request) {
		logger.info("Received chat completion request for model: {}", request.getModel());

		if (Boolean.TRUE.equals(request.getStream())) {
			// Return streaming response
			Flux<String> stream = openCodeService.streamChatCompletion(request);
			return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(stream);
		}
		else {
			// Return regular response
			return openCodeService.createChatCompletion(request)
				.map(ResponseEntity::ok)
				.onErrorReturn(ResponseEntity.internalServerError().build());
		}
	}

	@GetMapping("/models")
	@Operation(summary = "List models", description = "Lists the currently available models")
	public ResponseEntity<?> listModels() {
		return ResponseEntity.ok(new ModelsResponse());
	}

	@GetMapping("/health")
	@Operation(summary = "Health check", description = "Check if the API is running")
	public ResponseEntity<?> health() {
		return ResponseEntity.ok(new HealthResponse("healthy", "OpenAI API Bridge for OpenCode"));
	}

	// Response classes
	static class ModelsResponse {

		private String object = "list";

		private List<Model> data;

		public ModelsResponse() {
			this.data = java.util.Arrays.asList(new Model("gpt-4", "opencode"), new Model("gpt-4-turbo", "opencode"),
					new Model("gpt-3.5-turbo", "opencode"), new Model("gpt-3.5-turbo-16k", "opencode"));
		}

		public String getObject() {
			return object;
		}

		public List<Model> getData() {
			return data;
		}

		static class Model {

			private String id;

			private String object = "model";

			private Long created;

			private String ownedBy;

			public Model(String id, String ownedBy) {
				this.id = id;
				this.created = System.currentTimeMillis() / 1000;
				this.ownedBy = ownedBy;
			}

			public String getId() {
				return id;
			}

			public String getObject() {
				return object;
			}

			public Long getCreated() {
				return created;
			}

			public String getOwnedBy() {
				return ownedBy;
			}

		}

	}

	static class HealthResponse {

		private String status;

		private String service;

		public HealthResponse(String status, String service) {
			this.status = status;
			this.service = service;
		}

		public String getStatus() {
			return status;
		}

		public String getService() {
			return service;
		}

	}

}