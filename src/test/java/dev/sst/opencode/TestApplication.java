package dev.sst.opencode;

import dev.sst.opencode.client.OpenCodeClient;
import dev.sst.opencode.config.OpenCodeConfig;
import dev.sst.opencode.models.*;
import dev.sst.opencode.spring.OpenCodeRestTemplate;

/**
 * Simple test application to verify the SDK works
 */
public class TestApplication {

	public static void main(String[] args) {
		System.out.println("Testing OpenCode Java SDK...\n");

		// Test 1: Create OpenCodeClient directly
		System.out.println("1. Testing OpenCodeClient:");
		OpenCodeConfig config = OpenCodeConfig.builder()
			.baseUrl("http://localhost:8080")
			.apiKey("test-api-key")
			.workingDirectory("/home/user/project")
			.build();

		OpenCodeClient client = new OpenCodeClient(config);
		System.out.println("   ✓ OpenCodeClient created successfully");
		System.out.println("   - Sessions service: " + (client.getSessions() != null ? "Available" : "Not available"));
		System.out.println("   - Files service: " + (client.getFiles() != null ? "Available" : "Not available"));
		System.out
			.println("   - Config service: " + (client.getConfiguration() != null ? "Available" : "Not available"));
		System.out.println("   - Events service: " + (client.getEvents() != null ? "Available" : "Not available"));

		// Test 2: Create OpenCodeRestTemplate for Spring users
		System.out.println("\n2. Testing OpenCodeRestTemplate:");
		OpenCodeRestTemplate restTemplate = new OpenCodeRestTemplate(config);
		System.out.println("   ✓ OpenCodeRestTemplate created successfully");
		System.out
			.println("   - Underlying client: " + (restTemplate.getClient() != null ? "Available" : "Not available"));
		System.out.println(
				"   - Session service: " + (restTemplate.getSessionService() != null ? "Available" : "Not available"));
		System.out
			.println("   - File service: " + (restTemplate.getFileService() != null ? "Available" : "Not available"));
		System.out.println(
				"   - Config service: " + (restTemplate.getConfigService() != null ? "Available" : "Not available"));
		System.out
			.println("   - Event service: " + (restTemplate.getEventService() != null ? "Available" : "Not available"));

		// Test 3: Verify builder patterns
		System.out.println("\n3. Testing Builder patterns:");
		OpenCodeRestTemplate builtTemplate = OpenCodeRestTemplate.builder()
			.baseUrl("http://localhost:8080")
			.apiKey("test-key")
			.workingDirectory("/test/dir")
			.build();
		System.out.println("   ✓ OpenCodeRestTemplate builder works");

		System.out.println("\n✅ All basic tests passed!");
		System.out.println("\nNote: This test verifies the SDK compiles and initializes correctly.");
		System.out.println("To test actual API calls, ensure an OpenCode server is running on localhost:8080");
	}

}