package dev.sst.opencode.services;

import dev.sst.opencode.models.Project;

import java.util.List;

/**
 * Service interface for project operations
 */
public interface ProjectService {

	/**
	 * List all projects
	 */
	List<Project> listProjects();

	/**
	 * Get the current project
	 */
	Project getCurrentProject();

}