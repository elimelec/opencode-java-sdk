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
@Schema(description = "Application information")
public class AppInfo {

	@JsonProperty("version")
	@Schema(description = "Application version")
	private String version;

	@JsonProperty("name")
	@Schema(description = "Application name")
	private String name;

	@JsonProperty("initialized")
	@Schema(description = "Whether the application is initialized")
	private boolean initialized;

	@JsonProperty("status")
	@Schema(description = "Application status")
	private String status;

}