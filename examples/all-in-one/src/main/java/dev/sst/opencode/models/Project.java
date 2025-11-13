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
@Schema(description = "OpenCode Project")
public class Project {

	@JsonProperty("id")
	@Schema(description = "Project ID")
	private String id;

	@JsonProperty("name")
	@Schema(description = "Project name")
	private String name;

	@JsonProperty("directory")
	@Schema(description = "Project directory path")
	private String directory;

	@JsonProperty("description")
	@Schema(description = "Project description")
	private String description;

	@JsonProperty("created")
	@Schema(description = "Creation timestamp")
	private Long created;

	@JsonProperty("updated")
	@Schema(description = "Last update timestamp")
	private Long updated;

	@JsonProperty("sessionCount")
	@Schema(description = "Number of sessions in project")
	private Integer sessionCount;

	@JsonProperty("isCurrent")
	@Schema(description = "Whether this is the current project")
	private boolean isCurrent;

}