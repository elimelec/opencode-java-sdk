package dev.sst.opencode.services;

import dev.sst.opencode.models.*;
import dev.sst.opencode.models.requests.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for session operations
 */
public interface SessionService {

	/**
	 * Create a new session
	 */
	Session createSession(SessionCreateRequest request);

	/**
	 * List all sessions
	 */
	List<Session> listSessions();

	/**
	 * Get a specific session
	 */
	Session getSession(String sessionId);

	/**
	 * Update a session
	 */
	Session updateSession(String sessionId, String title);

	/**
	 * Delete a session
	 */
	void deleteSession(String sessionId);

	/**
	 * Send a prompt to a session
	 */
	Message sendPrompt(String sessionId, PromptRequest request);

	/**
	 * Send a prompt asynchronously
	 */
	CompletableFuture<Message> sendPromptAsync(String sessionId, PromptRequest request);

	/**
	 * Get all messages for a session
	 */
	List<Message> getMessages(String sessionId);

	/**
	 * Get a specific message
	 */
	Message getMessage(String sessionId, String messageId);

	/**
	 * Execute a command in a session
	 */
	Message executeCommand(String sessionId, String command, String arguments);

	/**
	 * Share a session
	 */
	Session shareSession(String sessionId);

	/**
	 * Unshare a session
	 */
	Session unshareSession(String sessionId);

	/**
	 * Initialize a session (create AGENTS.md)
	 */
	void initializeSession(String sessionId, String providerId, String modelId);

	/**
	 * Abort a running session
	 */
	void abortSession(String sessionId);

	/**
	 * Revert to a previous message
	 */
	Session revertMessage(String sessionId, String messageId, String partId);

	/**
	 * Restore reverted messages
	 */
	Session unrevertMessages(String sessionId);

	/**
	 * Get session's children
	 */
	List<Session> getSessionChildren(String sessionId);

	/**
	 * Generate session summary
	 */
	SessionSummary summarizeSession(String sessionId, String providerId, String modelId);

	/**
	 * Execute shell command in session
	 */
	Message executeShellCommand(String sessionId, ShellRequest request);

	/**
	 * Respond to permission request
	 */
	void respondToPermission(String sessionId, String permissionId, PermissionResponse response);

}