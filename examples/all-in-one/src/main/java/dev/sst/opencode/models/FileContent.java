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
@Schema(description = "File content")
public class FileContent {

	@JsonProperty("path")
	@Schema(description = "File path")
	private String path;

	@JsonProperty("content")
	@Schema(description = "File content")
	private String content;

	@JsonProperty("mime")
	@Schema(description = "MIME type")
	private String mime;

	@JsonProperty("encoding")
	@Schema(description = "File encoding")
	private String encoding;

	@JsonProperty("size")
	@Schema(description = "File size in bytes")
	private Long size;

}