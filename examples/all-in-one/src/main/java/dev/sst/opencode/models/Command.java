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
@Schema(description = "Command definition")
public class Command {

	@JsonProperty("name")
	@Schema(description = "Command name", example = "/help")
	private String name;

	@JsonProperty("description")
	@Schema(description = "Command description")
	private String description;

	@JsonProperty("aliases")
	@Schema(description = "Command aliases")
	private List<String> aliases;

	@JsonProperty("category")
	@Schema(description = "Command category")
	private String category;

	@JsonProperty("usage")
	@Schema(description = "Usage example")
	private String usage;

	@JsonProperty("arguments")
	@Schema(description = "Command arguments")
	private List<CommandArgument> arguments;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommandArgument {

		@JsonProperty("name")
		private String name;

		@JsonProperty("type")
		private String type;

		@JsonProperty("required")
		private boolean required;

		@JsonProperty("description")
		private String description;

	}

}