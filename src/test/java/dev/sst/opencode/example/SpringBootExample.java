package dev.sst.opencode.example;

import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.PromptRequest;
import dev.sst.opencode.spring.OpenCodeRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Example Spring Boot application using OpenCode SDK
 *
 * Add to your application.yml: <pre>
 * opencode:
 *   base-url: http://localhost:8080
 *   working-directory: /path/to/your/project
 * </pre>
 */
@Slf4j
@SpringBootApplication
public class SpringBootExample {

	@Autowired
	private OpenCodeRestTemplate openCode;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExample.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return args -> {
			log.info("OpenCode Spring Boot Example");

			// Example 1: Create a session and send a prompt
			example1_BasicUsage();

			// Example 2: Subscribe to events
			example2_EventStreaming();

			// Example 3: File operations
			example3_FileOperations();

			// Example 4: Complex workflow
			example4_ComplexWorkflow();
		};
	}

	private void example1_BasicUsage() {
		log.info("=== Example 1: Basic Usage ===");

		// Create a new session
		Session session = openCode.createSession("Demo Session");
		log.info("Created session: {}", session.getId());

		// Send a prompt
		Message response = openCode.sendPrompt(session.getId(),
				"Write a Python function to calculate fibonacci numbers", "anthropic", "claude-3-5-sonnet-latest");

		log.info("Received response with {} parts", response.getParts().size());

		// Print text parts
		response.getParts()
			.stream()
			.filter(part -> part instanceof Message.TextPart)
			.map(part -> (Message.TextPart) part)
			.forEach(text -> log.info("Text: {}", text.getText()));
	}

	private void example2_EventStreaming() throws InterruptedException {
		log.info("=== Example 2: Event Streaming ===");

		CountDownLatch latch = new CountDownLatch(1);

		// Subscribe to events
		Flux<OpenCodeEvent> eventStream = openCode.subscribeToEvents();

		eventStream.take(10) // Take first 10 events
			.doOnNext(event -> {
				log.info("Event: {} - {}", event.getType(), event.getProperties());

				// Handle specific event types
				if (event.isMessageEvent()) {
					handleMessageEvent(event);
				}
				else if (event.isToolEvent()) {
					handleToolEvent(event);
				}
			})
			.doOnComplete(() -> {
				log.info("Event stream completed");
				latch.countDown();
			})
			.doOnError(error -> {
				log.error("Event stream error", error);
				latch.countDown();
			})
			.subscribe();

		// Wait a bit for events
		Thread.sleep(5000);
		latch.countDown();
		latch.await();
	}

	private void handleMessageEvent(OpenCodeEvent event) {
		String messageId = event.getProperty("messageID", String.class);
		log.info("Processing message event: {}", messageId);
	}

	private void handleToolEvent(OpenCodeEvent event) {
		String tool = event.getProperty("tool", String.class);
		String status = event.getProperty("status", String.class);
		log.info("Tool {} status: {}", tool, status);
	}

	private void example3_FileOperations() {
		log.info("=== Example 3: File Operations ===");

		// List files in current directory
		List<FileNode> files = openCode.listFiles(".");
		log.info("Found {} files/directories", files.size());

		files.stream()
			.limit(5)
			.forEach(file -> log.info("  {} - {}", file.isDirectory() ? "[DIR]" : "[FILE]", file.getName()));

		// Search for text in files
		List<SearchMatch> matches = openCode.searchText("TODO");
		log.info("Found {} files with TODOs", matches.size());

		matches.stream()
			.limit(3)
			.forEach(match -> log.info("  {} has {} matches", match.getPath(), match.getMatches().size()));

		// Find files by name
		List<String> javaFiles = openCode.findFiles("*.java");
		log.info("Found {} Java files", javaFiles.size());
	}

	private void example4_ComplexWorkflow() {
		log.info("=== Example 4: Complex Workflow ===");

		// Create a session
		Session session = openCode.createSession("Code Review Session");

		// Send initial prompt with file context
		PromptRequest promptRequest = PromptRequest.builder()
			.text(List.of(PromptRequest.TextContent.builder()
				.text("Please review this code and suggest improvements")
				.build()))
			.files(List.of(PromptRequest.FileContent.builder().path("src/main/java/Example.java").build()))
			.model(PromptRequest.ModelConfig.builder()
				.providerId("anthropic")
				.modelId("claude-3-5-sonnet-latest")
				.build())
			.agent("code-review")
			.build();

		Message response = openCode.sendPrompt(session.getId(), promptRequest);

		// Process the response
		processAssistantResponse(response);

		// Follow up with another question
		Message followUp = openCode.sendPrompt(session.getId(), "Can you also check for security issues?", "anthropic",
				"claude-3-5-sonnet-latest");

		processAssistantResponse(followUp);

		// Clean up
		openCode.deleteSession(session.getId());
		log.info("Session deleted");
	}

	private void processAssistantResponse(Message message) {
		if (message.getInfo() instanceof Message.AssistantMessage) {
			Message.AssistantMessage assistant = (Message.AssistantMessage) message.getInfo();

			log.info("Status: {}", assistant.getStatus());

			if (assistant.getUsage() != null) {
				log.info("Tokens used - Input: {}, Output: {}", assistant.getUsage().getInput(),
						assistant.getUsage().getOutput());
			}

			// Process parts
			message.getParts().forEach(part -> {
				if (part instanceof Message.TextPart) {
					Message.TextPart text = (Message.TextPart) part;
					log.info("Assistant says: {}",
							text.getText().substring(0, Math.min(100, text.getText().length())) + "...");
				}
				else if (part instanceof Message.ToolPart) {
					Message.ToolPart tool = (Message.ToolPart) part;
					log.info("Tool executed: {} - Status: {}", tool.getTool(), tool.getState().getStatus());
				}
			});
		}
	}

}