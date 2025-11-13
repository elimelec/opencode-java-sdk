package dev.sst.opencode.bridge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public class ChatCompletionRequest {

	@NotNull
	private String model;

	@NotNull
	@Size(min = 1)
	private List<ChatMessage> messages;

	private Double temperature;

	@JsonProperty("max_tokens")
	private Integer maxTokens;

	@JsonProperty("top_p")
	private Double topP;

	private Integer n;

	private Boolean stream;

	private String stop;

	@JsonProperty("presence_penalty")
	private Double presencePenalty;

	@JsonProperty("frequency_penalty")
	private Double frequencyPenalty;

	private Map<String, Integer> logitBias;

	private String user;

	@JsonProperty("response_format")
	private ResponseFormat responseFormat;

	private Integer seed;

	private List<Tool> tools;

	@JsonProperty("tool_choice")
	private Object toolChoice;

	// Getters and setters
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Double getTopP() {
		return topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public Integer getN() {
		return n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	public Boolean getStream() {
		return stream;
	}

	public void setStream(Boolean stream) {
		this.stream = stream;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public Double getPresencePenalty() {
		return presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public Double getFrequencyPenalty() {
		return frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public Map<String, Integer> getLogitBias() {
		return logitBias;
	}

	public void setLogitBias(Map<String, Integer> logitBias) {
		this.logitBias = logitBias;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(ResponseFormat responseFormat) {
		this.responseFormat = responseFormat;
	}

	public Integer getSeed() {
		return seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public Object getToolChoice() {
		return toolChoice;
	}

	public void setToolChoice(Object toolChoice) {
		this.toolChoice = toolChoice;
	}

	public static class ChatMessage {

		private String role;

		private String content;

		private String name;

		@JsonProperty("tool_calls")
		private List<ToolCall> toolCalls;

		@JsonProperty("tool_call_id")
		private String toolCallId;

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<ToolCall> getToolCalls() {
			return toolCalls;
		}

		public void setToolCalls(List<ToolCall> toolCalls) {
			this.toolCalls = toolCalls;
		}

		public String getToolCallId() {
			return toolCallId;
		}

		public void setToolCallId(String toolCallId) {
			this.toolCallId = toolCallId;
		}

	}

	public static class ResponseFormat {

		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class Tool {

		private String type;

		private ToolFunction function;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ToolFunction getFunction() {
			return function;
		}

		public void setFunction(ToolFunction function) {
			this.function = function;
		}

	}

	public static class ToolFunction {

		private String name;

		private String description;

		private Map<String, Object> parameters;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Map<String, Object> getParameters() {
			return parameters;
		}

		public void setParameters(Map<String, Object> parameters) {
			this.parameters = parameters;
		}

	}

	public static class ToolCall {

		private String id;

		private String type;

		private ToolFunction function;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ToolFunction getFunction() {
			return function;
		}

		public void setFunction(ToolFunction function) {
			this.function = function;
		}

	}

}