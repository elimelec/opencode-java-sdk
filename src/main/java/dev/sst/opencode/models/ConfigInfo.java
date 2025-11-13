package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OpenCode configuration information")
public class ConfigInfo {

	@JsonProperty("version")
	@Schema(description = "OpenCode version")
	private String version;

	@JsonProperty("providers")
	@Schema(description = "Available providers")
	private List<String> providers;

	@JsonProperty("features")
	@Schema(description = "Enabled features")
	private Map<String, Boolean> features;

	@JsonProperty("limits")
	@Schema(description = "Configuration limits")
	private Limits limits;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Limits {

		@JsonProperty("maxFileSize")
		private Long maxFileSize;

		@JsonProperty("maxSessionMessages")
		private Integer maxSessionMessages;

		@JsonProperty("maxConcurrentSessions")
		private Integer maxConcurrentSessions;

	}

}