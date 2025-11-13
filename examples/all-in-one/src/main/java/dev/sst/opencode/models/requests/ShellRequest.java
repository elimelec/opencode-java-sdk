package dev.sst.opencode.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Shell command request")
public class ShellRequest {

	@JsonProperty("command")
	@Schema(description = "Shell command to execute", required = true)
	private String command;

	@JsonProperty("agent")
	@Schema(description = "Agent to use for execution", required = true)
	private String agent;

	@JsonProperty("workingDirectory")
	@Schema(description = "Working directory for command execution")
	private String workingDirectory;

	@JsonProperty("timeout")
	@Schema(description = "Command timeout in milliseconds")
	private Long timeout;

	@JsonProperty("environment")
	@Schema(description = "Environment variables")
	private java.util.Map<String, String> environment;

}