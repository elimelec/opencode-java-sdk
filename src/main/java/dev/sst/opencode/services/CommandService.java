package dev.sst.opencode.services;

import dev.sst.opencode.models.Agent;
import dev.sst.opencode.models.Command;

import java.util.List;

/**
 * Service interface for command and agent operations
 */
public interface CommandService {

	/**
	 * List all available commands
	 */
	List<Command> listCommands();

	/**
	 * Get a specific command by name
	 */
	Command getCommand(String name);

	/**
	 * List all available agents/modes
	 */
	List<Agent> listAgents();

	/**
	 * Get a specific agent by ID
	 */
	Agent getAgent(String agentId);

}