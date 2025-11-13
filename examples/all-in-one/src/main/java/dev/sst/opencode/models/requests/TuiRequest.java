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
@Schema(description = "TUI control request")
public class TuiRequest {

	@JsonProperty("text")
	@Schema(description = "Text content")
	private String text;

	@JsonProperty("command")
	@Schema(description = "Command to execute")
	private String command;

	@JsonProperty("message")
	@Schema(description = "Toast message")
	private String message;

	@JsonProperty("variant")
	@Schema(description = "Toast variant (info, success, warning, error)")
	private String variant;

	@JsonProperty("duration")
	@Schema(description = "Toast duration in milliseconds")
	private Integer duration;

	@JsonProperty("response")
	@Schema(description = "Control response data")
	private Object response;

}