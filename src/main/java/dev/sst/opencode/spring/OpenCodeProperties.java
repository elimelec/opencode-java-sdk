package dev.sst.opencode.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Spring Boot configuration properties for OpenCode
 *
 * Example configuration in application.yml: <pre>
 * opencode:
 *   enabled: true
 *   base-url: http://localhost:8080
 *   api-key: your-api-key-here
 *   working-directory: /home/user/project
 *   timeout: 30000
 *   max-retries: 3
 *   event-stream:
 *     enabled: true
 *     reconnect-delay: 5000
 * </pre>
 */
@Data
@Validated
@ConfigurationProperties(prefix = "opencode")
public class OpenCodeProperties {

	/**
	 * Enable or disable OpenCode integration
	 */
	private boolean enabled = true;

	/**
	 * Base URL of the OpenCode server
	 */
	@NotBlank
	private String baseUrl = "http://localhost:8080";

	/**
	 * API key for authentication (optional)
	 */
	private String apiKey;

	/**
	 * Working directory for file operations
	 */
	private String workingDirectory;

	/**
	 * Request timeout in milliseconds
	 */
	@Positive
	private int timeout = 30000;

	/**
	 * Maximum number of retries for failed requests
	 */
	@Positive
	private int maxRetries = 3;

	/**
	 * Event stream configuration
	 */
	private EventStreamProperties eventStream = new EventStreamProperties();

	@Data
	public static class EventStreamProperties {

		/**
		 * Enable event streaming via SSE
		 */
		private boolean enabled = true;

		/**
		 * Reconnect delay in milliseconds when connection is lost
		 */
		@Positive
		private int reconnectDelay = 5000;

		/**
		 * Maximum reconnection attempts
		 */
		@Positive
		private int maxReconnectAttempts = 10;

	}

}