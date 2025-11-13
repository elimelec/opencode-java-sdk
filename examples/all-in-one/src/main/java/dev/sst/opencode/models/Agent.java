package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent/Mode definition")
public class Agent {

	@JsonProperty("id")
	@Schema(description = "Agent ID", example = "code")
	private String id;

	@JsonProperty("name")
	@Schema(description = "Agent name", example = "Code Mode")
	private String name;

	@JsonProperty("description")
	@Schema(description = "Agent description")
	private String description;

	@JsonProperty("capabilities")
	@Schema(description = "Agent capabilities")
	private List<String> capabilities;

	@JsonProperty("models")
	@Schema(description = "Supported models")
	private List<String> models;

	@JsonProperty("providers")
	@Schema(description = "Supported providers")
	private List<String> providers;

	@JsonProperty("isDefault")
	@Schema(description = "Whether this is the default agent")
	private boolean isDefault;

	@JsonProperty("enabled")
	@Schema(description = "Whether the agent is enabled")
	private boolean enabled;

}