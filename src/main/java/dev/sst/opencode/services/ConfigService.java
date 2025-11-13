package dev.sst.opencode.services;

import dev.sst.opencode.models.ConfigInfo;
import dev.sst.opencode.models.ProvidersResponse;

/**
 * Service interface for configuration operations
 */
public interface ConfigService {

	/**
	 * Get configuration info
	 */
	ConfigInfo getConfig();

	/**
	 * List available providers
	 */
	ProvidersResponse listProviders();

	/**
	 * Get current working directory path
	 */
	String getWorkingDirectory();

	/**
	 * Set authentication credentials
	 */
	void setAuthentication(String providerId, String credentials);

}