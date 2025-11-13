package dev.sst.opencode.spring;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.models.ConfigInfo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Spring Boot Actuator health indicator for OpenCode
 */
public class OpenCodeHealthIndicator implements HealthIndicator {

	private final OpenCodeClient client;

	public OpenCodeHealthIndicator(OpenCodeClient client) {
		this.client = client;
	}

	@Override
	public Health health() {
		try {
			ConfigInfo config = client.getConfiguration().getConfig();

			return Health.up()
				.withDetail("baseUrl", client.getBaseUrl())
				.withDetail("version", config.getVersion())
				.withDetail("providers", config.getProviders())
				.build();
		}
		catch (Exception e) {
			return Health.down().withDetail("baseUrl", client.getBaseUrl()).withException(e).build();
		}
	}

}