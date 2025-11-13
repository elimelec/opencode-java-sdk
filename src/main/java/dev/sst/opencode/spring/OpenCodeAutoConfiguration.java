package dev.sst.opencode.spring;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.config.OpenCodeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Boot Auto Configuration for OpenCode SDK
 *
 * This configuration is automatically loaded when: 1. opencode-java-sdk is on the
 * classpath 2. opencode.enabled is not set to false
 *
 * Usage in application.yml: <pre>
 * opencode:
 *   base-url: http://localhost:8080
 *   api-key: your-api-key
 *   working-directory: /path/to/project
 *   timeout: 30000
 * </pre>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({ OpenCodeClient.class, RestTemplate.class })
@ConditionalOnProperty(prefix = "opencode", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OpenCodeProperties.class)
public class OpenCodeAutoConfiguration {

	private final OpenCodeProperties properties;

	public OpenCodeAutoConfiguration(OpenCodeProperties properties) {
		this.properties = properties;
		log.info("Initializing OpenCode Auto Configuration with base URL: {}", properties.getBaseUrl());
	}

	/**
	 * Creates the OpenCodeConfig bean from properties
	 */
	@Bean
	@ConditionalOnMissingBean
	public OpenCodeConfig openCodeConfig() {
		return OpenCodeConfig.builder()
			.baseUrl(properties.getBaseUrl())
			.apiKey(properties.getApiKey())
			.workingDirectory(properties.getWorkingDirectory())
			.timeout(properties.getTimeout())
			.maxRetries(properties.getMaxRetries())
			.build();
	}

	/**
	 * Creates the main OpenCodeClient bean
	 */
	@Bean
	@ConditionalOnMissingBean
	public OpenCodeClient openCodeClient(OpenCodeConfig config) {
		log.info("Creating OpenCodeClient bean");
		return new OpenCodeClient(config);
	}

	/**
	 * Creates the OpenCodeRestTemplate bean for Spring users
	 */
	@Bean
	@ConditionalOnMissingBean
	public OpenCodeRestTemplate openCodeRestTemplate(OpenCodeConfig config) {
		log.info("Creating OpenCodeRestTemplate bean");
		return new OpenCodeRestTemplate(config);
	}

	/**
	 * Creates a default RestTemplate if none exists
	 */
	@Bean
	@ConditionalOnMissingBean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * Health indicator for OpenCode connection
	 */
	@Configuration
	@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
	public static class OpenCodeHealthConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public OpenCodeHealthIndicator openCodeHealthIndicator(OpenCodeClient client) {
			return new OpenCodeHealthIndicator(client);
		}

	}

}