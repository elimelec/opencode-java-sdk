package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File or directory node")
public class FileNode {

	@JsonProperty("name")
	@Schema(description = "File or directory name")
	private String name;

	@JsonProperty("path")
	@Schema(description = "Full path")
	private String path;

	@JsonProperty("type")
	@Schema(description = "Node type", allowableValues = { "file", "directory" })
	private String type;

	@JsonProperty("size")
	@Schema(description = "Size in bytes (for files)")
	private Long size;

	@JsonProperty("modified")
	@Schema(description = "Last modified timestamp")
	private Long modified;

	@JsonProperty("isDirectory")
	public boolean isDirectory() {
		return "directory".equals(type);
	}

	@JsonProperty("isFile")
	public boolean isFile() {
		return "file".equals(type);
	}

}