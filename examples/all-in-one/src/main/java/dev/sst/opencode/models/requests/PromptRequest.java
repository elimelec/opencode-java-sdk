package dev.sst.opencode.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to send a prompt to a session")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromptRequest {

	@JsonProperty("messageID")
	@Schema(description = "Optional message ID")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String messageId;

	@JsonProperty("model")
	@Schema(description = "Model configuration")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ModelConfig model;

	@JsonProperty("agent")
	@Schema(description = "Agent to use", defaultValue = "build")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder.Default
	private String agent = "build";

	@JsonProperty("system")
	@Schema(description = "System prompt override")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String system;

	@JsonProperty("parts")
	@Schema(description = "Message parts (text, images, files)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Object> parts;

	@JsonProperty("text")
	@Schema(description = "Text prompt content (deprecated, use parts)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Deprecated
	private List<TextContent> text;

	@JsonProperty("images")
	@Schema(description = "Image attachments (deprecated, use parts)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Deprecated
	private List<ImageContent> images;

	@JsonProperty("files")
	@Schema(description = "File attachments (deprecated, use parts)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Deprecated
	private List<FileContent> files;

	@JsonProperty("providerID")
	@Schema(description = "Provider ID (deprecated, use model.providerID)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Deprecated
	private String providerId;

	@JsonProperty("modelID")
	@Schema(description = "Model ID (deprecated, use model.modelID)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Deprecated
	private String modelId;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModelConfig {

		@JsonProperty("providerID")
		@NotNull
		private String providerId;

		@JsonProperty("modelID")
		@NotNull
		private String modelId;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TextContent {

		@JsonProperty("type")
		@Builder.Default
		private String type = "text";

		@JsonProperty("text")
		@NotNull
		private String text;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ImageContent {

		@JsonProperty("type")
		@Builder.Default
		private String type = "image";

		@JsonProperty("mime")
		private String mime;

		@JsonProperty("url")
		private String url;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FileContent {

		@JsonProperty("path")
		@NotNull
		private String path;

		@JsonProperty("ranges")
		private List<Range> ranges;

		@JsonProperty("mime")
		private String mime;

		@JsonProperty("content")
		private String content;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Range {

		@JsonProperty("start")
		private Integer start;

		@JsonProperty("end")
		private Integer end;

	}

	/**
	 * Convenience builder for simple text prompts
	 */
	public static PromptRequest ofText(String text, String providerId, String modelId) {
		TextContent textContent = TextContent.builder().text(text).build();
		return PromptRequest.builder()
			.parts(List.of(textContent))
			.model(ModelConfig.builder().providerId(providerId).modelId(modelId).build())
			.build();
	}

}