package dev.sst.opencode.services;

import dev.sst.opencode.models.FileContent;
import dev.sst.opencode.models.FileNode;
import dev.sst.opencode.models.SearchMatch;

import java.util.List;

/**
 * Service interface for file operations
 */
public interface FileService {

	/**
	 * Read a file
	 */
	FileContent readFile(String path);

	/**
	 * List files in a directory
	 */
	List<FileNode> listFiles(String path);

	/**
	 * Get file status (git-like)
	 */
	List<FileNode> getFileStatus();

	/**
	 * Search for text in files
	 */
	List<SearchMatch> searchText(String pattern);

	/**
	 * Find files by name
	 */
	List<String> findFiles(String query);

	/**
	 * Find workspace symbols
	 */
	List<Object> findSymbols(String query);

}