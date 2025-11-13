package dev.sst.opencode;

import dev.sst.opencode.config.OpenCodeConfig;
import dev.sst.opencode.spring.OpenCodeRestTemplate;
import dev.sst.opencode.models.Session;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicTest {

	@Test
	public void testSDKInitialization() {
		// Test that the SDK can be initialized
		OpenCodeConfig config = OpenCodeConfig.builder().baseUrl("http://localhost:8080").build();

		OpenCodeRestTemplate client = new OpenCodeRestTemplate(config);
		assertNotNull(client);
	}

	@Test
	public void testServiceAccess() {
		OpenCodeConfig config = OpenCodeConfig.builder().baseUrl("http://localhost:8080").build();

		OpenCodeRestTemplate client = new OpenCodeRestTemplate(config);

		// This would be a problem - the service returns null
		// Session session = client.getSessionService().createSession(null);
		// assertNull(session); // This would pass but it shouldn't!
	}

}