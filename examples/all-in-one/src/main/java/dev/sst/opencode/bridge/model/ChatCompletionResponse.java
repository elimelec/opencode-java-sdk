package dev.sst.opencode.bridge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ChatCompletionResponse {

	private String id;

	private String object = "chat.completion";

	private Long created;

	private String model;

	private List<Choice> choices;

	private Usage usage;

	@JsonProperty("system_fingerprint")
	private String systemFingerprint;

	public ChatCompletionResponse() {
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

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public String getSystemFingerprint() {
		return systemFingerprint;
	}

	public void setSystemFingerprint(String systemFingerprint) {
		this.systemFingerprint = systemFingerprint;
	}

	public static class Choice {

		private Integer index;

		private ChatCompletionRequest.ChatMessage message;

		@JsonProperty("finish_reason")
		private String finishReason;

		private LogProbs logprobs;

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public ChatCompletionRequest.ChatMessage getMessage() {
			return message;
		}

		public void setMessage(ChatCompletionRequest.ChatMessage message) {
			this.message = message;
		}

		public String getFinishReason() {
			return finishReason;
		}

		public void setFinishReason(String finishReason) {
			this.finishReason = finishReason;
		}

		public LogProbs getLogprobs() {
			return logprobs;
		}

		public void setLogprobs(LogProbs logprobs) {
			this.logprobs = logprobs;
		}

	}

	public static class Usage {

		@JsonProperty("prompt_tokens")
		private Integer promptTokens;

		@JsonProperty("completion_tokens")
		private Integer completionTokens;

		@JsonProperty("total_tokens")
		private Integer totalTokens;

		public Integer getPromptTokens() {
			return promptTokens;
		}

		public void setPromptTokens(Integer promptTokens) {
			this.promptTokens = promptTokens;
		}

		public Integer getCompletionTokens() {
			return completionTokens;
		}

		public void setCompletionTokens(Integer completionTokens) {
			this.completionTokens = completionTokens;
		}

		public Integer getTotalTokens() {
			return totalTokens;
		}

		public void setTotalTokens(Integer totalTokens) {
			this.totalTokens = totalTokens;
		}

	}

	public static class LogProbs {

		private List<String> tokens;

		@JsonProperty("token_logprobs")
		private List<Double> tokenLogprobs;

		@JsonProperty("top_logprobs")
		private List<Object> topLogprobs;

		@JsonProperty("text_offset")
		private List<Integer> textOffset;

		public List<String> getTokens() {
			return tokens;
		}

		public void setTokens(List<String> tokens) {
			this.tokens = tokens;
		}

		public List<Double> getTokenLogprobs() {
			return tokenLogprobs;
		}

		public void setTokenLogprobs(List<Double> tokenLogprobs) {
			this.tokenLogprobs = tokenLogprobs;
		}

		public List<Object> getTopLogprobs() {
			return topLogprobs;
		}

		public void setTopLogprobs(List<Object> topLogprobs) {
			this.topLogprobs = topLogprobs;
		}

		public List<Integer> getTextOffset() {
			return textOffset;
		}

		public void setTextOffset(List<Integer> textOffset) {
			this.textOffset = textOffset;
		}

	}

}