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
@Schema(description = "Log entry request")
public class LogRequest {

	@JsonProperty("level")
	@Schema(description = "Log level (debug, info, warn, error)", required = true)
	private String level;

	@JsonProperty("message")
	@Schema(description = "Log message", required = true)
	private String message;

	@JsonProperty("service")
	@Schema(description = "Log service/component")
	private String service;

	@JsonProperty("metadata")
	@Schema(description = "Additional metadata")
	private java.util.Map<String, Object> metadata;

	@JsonProperty("timestamp")
	@Schema(description = "Log timestamp")
	private Long timestamp;

}