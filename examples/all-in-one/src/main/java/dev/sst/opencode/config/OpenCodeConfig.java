package dev.sst.opencode.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration for OpenCode client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenCodeConfig {

	@Builder.Default
	private String baseUrl = "http://localhost:8080";

	private String apiKey;

	private String workingDirectory;

	@Builder.Default
	private int timeout = 30000;

	@Builder.Default
	private int maxRetries = 3;

	@Builder.Default
	private boolean sslVerification = true;

	/**
	 * Create config from environment variables
	 */
	public static OpenCodeConfig fromEnvironment() {
		return OpenCodeConfig.builder()
			.baseUrl(System.getenv().getOrDefault("OPENCODE_BASE_URL", "http://localhost:8080"))
			.apiKey(System.getenv("OPENCODE_API_KEY"))
			.workingDirectory(System.getenv("OPENCODE_WORKING_DIR"))
			.build();
	}

}