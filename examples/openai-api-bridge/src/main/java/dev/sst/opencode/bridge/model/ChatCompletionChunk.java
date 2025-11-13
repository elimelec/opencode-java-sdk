package dev.sst.opencode.bridge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ChatCompletionChunk {

	private String id;

	private String object = "chat.completion.chunk";

	private Long created;

	private String model;

	private List<ChunkChoice> choices;

	@JsonProperty("system_fingerprint")
	private String systemFingerprint;

	public ChatCompletionChunk() {
		this.created = System.currentTimeMillis() / 1000;
		this.id = "chatcmpl-" + System.nanoTime();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChunkChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<ChunkChoice> choices) {
		this.choices = choices;
	}

	public String getSystemFingerprint() {
		return systemFingerprint;
	}

	public void setSystemFingerprint(String systemFingerprint) {
		this.systemFingerprint = systemFingerprint;
	}

	public static class ChunkChoice {

		private Integer index;

		private Delta delta;

		@JsonProperty("finish_reason")
		private String finishReason;

		private ChatCompletionResponse.LogProbs logprobs;

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public Delta getDelta() {
			return delta;
		}

		public void setDelta(Delta delta) {
			this.delta = delta;
		}

		public String getFinishReason() {
			return finishReason;
		}

		public void setFinishReason(String finishReason) {
			this.finishReason = finishReason;
		}

		public ChatCompletionResponse.LogProbs getLogprobs() {
			return logprobs;
		}

		public void setLogprobs(ChatCompletionResponse.LogProbs logprobs) {
			this.logprobs = logprobs;
		}

	}

	public static class Delta {

		private String role;

		private String content;

		@JsonProperty("tool_calls")
		private List<ChatCompletionRequest.ToolCall> toolCalls;

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<ChatCompletionRequest.ToolCall> getToolCalls() {
			return toolCalls;
		}

		public void setToolCalls(List<ChatCompletionRequest.ToolCall> toolCalls) {
			this.toolCalls = toolCalls;
		}

	}

}