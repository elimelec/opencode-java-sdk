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
@Schema(description = "Session summary")
public class SessionSummary {

	@JsonProperty("sessionId")
	@Schema(description = "Session ID")
	private String sessionId;

	@JsonProperty("title")
	@Schema(description = "Summary title")
	private String title;

	@JsonProperty("summary")
	@Schema(description = "Text summary of the session")
	private String summary;

	@JsonProperty("keyPoints")
	@Schema(description = "Key points from the session")
	private List<String> keyPoints;

	@JsonProperty("filesModified")
	@Schema(description = "List of files modified")
	private List<String> filesModified;

	@JsonProperty("commandsExecuted")
	@Schema(description = "List of commands executed")
	private List<String> commandsExecuted;

	@JsonProperty("messageCount")
	@Schema(description = "Total number of messages")
	private int messageCount;

	@JsonProperty("duration")
	@Schema(description = "Session duration in milliseconds")
	private Long duration;

	@JsonProperty("timestamp")
	@Schema(description = "Summary generation timestamp")
	private Long timestamp;

}