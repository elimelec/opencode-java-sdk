package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Available providers response")
public class ProvidersResponse {

	@JsonProperty("providers")
	@Schema(description = "List of available providers")
	private List<Provider> providers;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Provider {

		@JsonProperty("id")
		@Schema(description = "Provider ID", example = "anthropic")
		private String id;

		@JsonProperty("name")
		@Schema(description = "Provider name", example = "Anthropic")
		private String name;

		@JsonProperty("env")
		@Schema(description = "Environment variables")
		private List<String> env;

		@JsonProperty("api")
		@Schema(description = "API endpoint")
		private String api;

		@JsonProperty("doc")
		@Schema(description = "Documentation URL")
		private String doc;

		@JsonProperty("npm")
		@Schema(description = "NPM package")
		private String npm;

		@JsonProperty("models")
		@Schema(description = "Available models")
		private Map<String, Model> models;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Model {

		@JsonProperty("id")
		@Schema(description = "Model ID", example = "claude-3-5-sonnet-latest")
		private String id;

		@JsonProperty("name")
		@Schema(description = "Model display name")
		private String name;

		@JsonProperty("attachment")
		@Schema(description = "Supports attachments")
		private Boolean attachment;

		@JsonProperty("reasoning")
		@Schema(description = "Supports reasoning")
		private Boolean reasoning;

		@JsonProperty("temperature")
		@Schema(description = "Supports temperature control")
		private Boolean temperature;

		@JsonProperty("tool_call")
		@Schema(description = "Supports tool calls")
		private Boolean toolCall;

		@JsonProperty("knowledge")
		@Schema(description = "Knowledge cutoff date")
		private String knowledge;

		@JsonProperty("release_date")
		@Schema(description = "Release date")
		private String releaseDate;

		@JsonProperty("last_updated")
		@Schema(description = "Last update date")
		private String lastUpdated;

		@JsonProperty("modalities")
		@Schema(description = "Input/output modalities")
		private Modalities modalities;

		@JsonProperty("open_weights")
		@Schema(description = "Has open weights")
		private Boolean openWeights;

		@JsonProperty("cost")
		@Schema(description = "Cost information")
		private Cost cost;

		@JsonProperty("limit")
		@Schema(description = "Token limits")
		private Limit limit;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Modalities {

		@JsonProperty("input")
		private List<String> input;

		@JsonProperty("output")
		private List<String> output;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Cost {

		@JsonProperty("input")
		private Double input;

		@JsonProperty("output")
		private Double output;

		@JsonProperty("cache_read")
		private Double cacheRead;

		@JsonProperty("cache_write")
		private Double cacheWrite;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Limit {

		@JsonProperty("context")
		private Integer context;

		@JsonProperty("output")
		private Integer output;

	}

}