package dev.sst.opencode.examples.chat.model;

public class SimpleChatResponse {

	private String content;

	private String sessionId;

	private String type;

	private String messageId;

	private boolean success = true;

	public SimpleChatResponse() {
	}

	public SimpleChatResponse(String content, String type) {
		this.content = content;
		this.type = type;
		this.success = !"ERROR".equals(type);
	}

	public static SimpleChatResponse error(String message) {
		SimpleChatResponse response = new SimpleChatResponse(message, "ERROR");
		response.setSuccess(false);
		return response;
	}

	public static SimpleChatResponse system(String message) {
		return new SimpleChatResponse(message, "SYSTEM");
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}