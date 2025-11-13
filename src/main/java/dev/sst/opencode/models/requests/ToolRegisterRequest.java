package dev.sst.opencode.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(description = "Tool registration request")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolRegisterRequest {

	@JsonProperty("id")
	@Schema(description = "Tool ID", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String id;

	@JsonProperty("name")
	@Schema(description = "Tool name", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;

	@JsonProperty("description")
	@Schema(description = "Tool description")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	@JsonProperty("callbackUrl")
	@Schema(description = "HTTP callback URL", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String callbackUrl;

	@JsonProperty("parameters")
	@Schema(description = "JSON Schema for tool parameters")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> parameters;

	@JsonProperty("authentication")
	@Schema(description = "Authentication configuration")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> authentication;

}