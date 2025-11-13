package dev.sst.opencode.bridge.model;

import java.util.ArrayList;
import java.util.List;

public class Session {

	private String id;

	private Long createdAt;

	private List<ChatCompletionRequest.ChatMessage> messages;

	public Session() {
		this.messages = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public List<ChatCompletionRequest.ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatCompletionRequest.ChatMessage> messages) {
		this.messages = messages;
	}

}