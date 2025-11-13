package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OpenCode Session")
public class Session {

	@JsonProperty("id")
	@Schema(description = "Session ID", example = "session_123abc")
	private String id;

	@JsonProperty("projectID")
	@Schema(description = "Project ID")
	private String projectId;

	@JsonProperty("directory")
	@Schema(description = "Working directory")
	private String directory;

	@JsonProperty("parentID")
	@Schema(description = "Parent session ID", required = false)
	private String parentId;

	@JsonProperty("title")
	@Schema(description = "Session title")
	private String title;

	@JsonProperty("version")
	@Schema(description = "Session version")
	private String version;

	@JsonProperty("share")
	@Schema(description = "Share information", required = false)
	private ShareInfo share;

	@JsonProperty("time")
	@Schema(description = "Timestamps")
	private TimeInfo time;

	@JsonProperty("revert")
	@Schema(description = "Revert information", required = false)
	private RevertInfo revert;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ShareInfo {

		@JsonProperty("url")
		private String url;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TimeInfo {

		@JsonProperty("created")
		private Long created;

		@JsonProperty("updated")
		private Long updated;

		public Instant getCreatedInstant() {
			return created != null ? Instant.ofEpochMilli(created) : null;
		}

		public Instant getUpdatedInstant() {
			return updated != null ? Instant.ofEpochMilli(updated) : null;
		}

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RevertInfo {

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("partID")
		private String partId;

		@JsonProperty("snapshot")
		private String snapshot;

		@JsonProperty("diff")
		private String diff;

	}

}