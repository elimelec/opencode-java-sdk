package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@Schema(description = "Message in a session")
public class Message {

	@JsonProperty("info")
	@Schema(description = "Message metadata")
	private MessageInfo info;

	@JsonProperty("parts")
	@Schema(description = "Message parts")
	private List<MessagePart> parts;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role")
	@JsonSubTypes({ @JsonSubTypes.Type(value = UserMessage.class, name = "user"),
			@JsonSubTypes.Type(value = AssistantMessage.class, name = "assistant"),
			@JsonSubTypes.Type(value = SystemMessage.class, name = "system") })
	public interface MessageInfo {

		String getId();

		String getSessionId();

		String getRole();

		TimeInfo getTime();

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "User message")
	public static class UserMessage implements MessageInfo {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("role")
		private final String role = "user";

		@JsonProperty("time")
		private TimeInfo time;

		@JsonProperty("text")
		private List<TextContent> text;

		@JsonProperty("files")
		private List<FileContent> files;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "Assistant message")
	public static class AssistantMessage implements MessageInfo {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("role")
		private final String role = "assistant";

		@JsonProperty("time")
		private TimeInfo time;

		@JsonProperty("model")
		private ModelInfo model;

		@JsonProperty("status")
		private String status;

		@JsonProperty("error")
		private ErrorInfo error;

		@JsonProperty("usage")
		private UsageInfo usage;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "System message")
	public static class SystemMessage implements MessageInfo {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("role")
		private final String role = "system";

		@JsonProperty("time")
		private TimeInfo time;

		@JsonProperty("text")
		private String text;

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

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TextContent {

		@JsonProperty("type")
		private String type;

		@JsonProperty("value")
		private String value;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FileContent {

		@JsonProperty("path")
		private String path;

		@JsonProperty("mime")
		private String mime;

		@JsonProperty("url")
		private String url;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SimpleMessagePart {

		@JsonProperty("type")
		private String type;

		@JsonProperty("content")
		private String content;

		@JsonProperty("id")
		private String id;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModelInfo {

		@JsonProperty("providerID")
		private String providerId;

		@JsonProperty("modelID")
		private String modelId;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ErrorInfo {

		@JsonProperty("type")
		private String type;

		@JsonProperty("message")
		private String message;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsageInfo {

		@JsonProperty("input")
		private Integer input;

		@JsonProperty("output")
		private Integer output;

	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
	@JsonSubTypes({ @JsonSubTypes.Type(value = TextPart.class, name = "text"),
			@JsonSubTypes.Type(value = ToolPart.class, name = "tool"),
			@JsonSubTypes.Type(value = FilePart.class, name = "file"),
			@JsonSubTypes.Type(value = SnapshotPart.class, name = "snapshot"),
			@JsonSubTypes.Type(value = PatchPart.class, name = "patch"),
			@JsonSubTypes.Type(value = StepStartPart.class, name = "step-start"),
			@JsonSubTypes.Type(value = StepEndPart.class, name = "step-end"),
			@JsonSubTypes.Type(value = StepFinishPart.class, name = "step-finish") })
	public interface MessagePart {

		String getId();

		String getSessionId();

		String getMessageId();

		String getType();

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TextPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "text";

		@JsonProperty("text")
		private String text;

		@JsonProperty("synthetic")
		private Boolean synthetic;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ToolPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "tool";

		@JsonProperty("callID")
		private String callId;

		@JsonProperty("tool")
		private String tool;

		@JsonProperty("state")
		private ToolState state;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FilePart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "file";

		@JsonProperty("mime")
		private String mime;

		@JsonProperty("filename")
		private String filename;

		@JsonProperty("url")
		private String url;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SnapshotPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "snapshot";

		@JsonProperty("snapshot")
		private String snapshot;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PatchPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "patch";

		@JsonProperty("hash")
		private String hash;

		@JsonProperty("files")
		private List<String> files;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PartTimeInfo {

		@JsonProperty("start")
		private Long start;

		@JsonProperty("end")
		private Long end;

	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "status")
	@JsonSubTypes({ @JsonSubTypes.Type(value = ToolStatePending.class, name = "pending"),
			@JsonSubTypes.Type(value = ToolStateRunning.class, name = "running"),
			@JsonSubTypes.Type(value = ToolStateCompleted.class, name = "completed"),
			@JsonSubTypes.Type(value = ToolStateError.class, name = "error") })
	public interface ToolState {

		String getStatus();

	}

	@Data
	@Builder
	@NoArgsConstructor
	public static class ToolStatePending implements ToolState {

		@JsonProperty("status")
		private final String status = "pending";

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ToolStateRunning implements ToolState {

		@JsonProperty("status")
		@Builder.Default
		private final String status = "running";

		@JsonProperty("input")
		private Map<String, Object> input;

		@JsonProperty("title")
		private String title;

		@JsonProperty("metadata")
		private Map<String, Object> metadata;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ToolStateCompleted implements ToolState {

		@JsonProperty("status")
		@Builder.Default
		private final String status = "completed";

		@JsonProperty("input")
		private Map<String, Object> input;

		@JsonProperty("output")
		private String output;

		@JsonProperty("title")
		private String title;

		@JsonProperty("metadata")
		private Map<String, Object> metadata;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ToolStateError implements ToolState {

		@JsonProperty("status")
		@Builder.Default
		private final String status = "error";

		@JsonProperty("input")
		private Map<String, Object> input;

		@JsonProperty("error")
		private String error;

		@JsonProperty("metadata")
		private Map<String, Object> metadata;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StepStartPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "step-start";

		@JsonProperty("title")
		private String title;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StepEndPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "step-end";

		@JsonProperty("title")
		private String title;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StepFinishPart implements MessagePart {

		@JsonProperty("id")
		private String id;

		@JsonProperty("sessionID")
		private String sessionId;

		@JsonProperty("messageID")
		private String messageId;

		@JsonProperty("type")
		private final String type = "step-finish";

		@JsonProperty("title")
		private String title;

		@JsonProperty("time")
		private PartTimeInfo time;

	}

}