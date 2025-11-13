package dev.sst.opencode.services;

import dev.sst.opencode.models.requests.LogRequest;

/**
 * Service interface for logging operations
 */
public interface LogService {

	/**
	 * Write a log entry to the server logs
	 */
	void log(LogRequest request);

	/**
	 * Write a debug log entry
	 */
	void debug(String message, String source);

	/**
	 * Write an info log entry
	 */
	void info(String message, String source);

	/**
	 * Write a warning log entry
	 */
	void warn(String message, String source);

	/**
	 * Write an error log entry
	 */
	void error(String message, String source);

}