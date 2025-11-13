package dev.sst.opencode.services;

import dev.sst.opencode.models.Tool;
import dev.sst.opencode.models.requests.ToolRegisterRequest;

import java.util.List;

/**
 * Service interface for experimental tool operations
 */
public interface ToolService {

	/**
	 * Register a new HTTP callback tool
	 */
	Tool registerTool(ToolRegisterRequest request);

	/**
	 * List all tool IDs (built-in and dynamic)
	 */
	List<String> listToolIds();

	/**
	 * List tools with JSON schema parameters for a provider/model
	 */
	List<Tool> listTools(String providerId, String modelId);

	/**
	 * Get a specific tool by ID
	 */
	Tool getTool(String toolId);

	/**
	 * Unregister a dynamic tool
	 */
	void unregisterTool(String toolId);

}