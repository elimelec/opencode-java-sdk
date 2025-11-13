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
@Schema(description = "Search match result")
public class SearchMatch {

	@JsonProperty("path")
	@Schema(description = "File path")
	private String path;

	@JsonProperty("matches")
	@Schema(description = "Match details")
	private List<Match> matches;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Match {

		@JsonProperty("line")
		@Schema(description = "Line number")
		private Integer line;

		@JsonProperty("column")
		@Schema(description = "Column number")
		private Integer column;

		@JsonProperty("text")
		@Schema(description = "Matched text")
		private String text;

		@JsonProperty("context")
		@Schema(description = "Surrounding context")
		private String context;

	}

}