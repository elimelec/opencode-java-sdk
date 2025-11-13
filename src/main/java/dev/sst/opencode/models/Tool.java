package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tool definition")
public class Tool {

	@JsonProperty("id")
	@Schema(description = "Tool ID", example = "bash")
	private String id;

	@JsonProperty("name")
	@Schema(description = "Tool name")
	private String name;

	@JsonProperty("description")
	@Schema(description = "Tool description")
	private String description;

	@JsonProperty("type")
	@Schema(description = "Tool type (built-in or dynamic)")
	private String type;

	@JsonProperty("parameters")
	@Schema(description = "JSON Schema for tool parameters")
	private Map<String, Object> parameters;

	@JsonProperty("enabled")
	@Schema(description = "Whether the tool is enabled")
	private boolean enabled;

	@JsonProperty("callbackUrl")
	@Schema(description = "Callback URL for HTTP tools")
	private String callbackUrl;

}