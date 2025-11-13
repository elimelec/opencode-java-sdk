package dev.sst.opencode.examples.chat.model;

public class SimpleChatMessage {

	private String content;

	private String providerId;

	private String modelId;

	private String type;

	public SimpleChatMessage() {
	}

	public SimpleChatMessage(String content, String providerId, String modelId, String type) {
		this.content = content;
		this.providerId = providerId;
		this.modelId = modelId;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}