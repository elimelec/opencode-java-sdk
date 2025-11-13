package dev.sst.opencode.services;

import dev.sst.opencode.models.requests.TuiRequest;

/**
 * Service interface for TUI (Terminal UI) control operations
 */
public interface TuiService {

	/**
	 * Append text to the prompt
	 */
	void appendPrompt(String text);

	/**
	 * Submit the current prompt
	 */
	void submitPrompt();

	/**
	 * Clear the prompt
	 */
	void clearPrompt();

	/**
	 * Open the help dialog
	 */
	void openHelp();

	/**
	 * Open the sessions dialog
	 */
	void openSessions();

	/**
	 * Open the themes dialog
	 */
	void openThemes();

	/**
	 * Open the models dialog
	 */
	void openModels();

	/**
	 * Execute a TUI command
	 */
	void executeCommand(String command);

	/**
	 * Show a toast notification
	 */
	void showToast(String message, String type, Integer duration);

	/**
	 * Get next TUI control request
	 */
	Object getNextControlRequest();

	/**
	 * Submit TUI control response
	 */
	void submitControlResponse(Object response);

}