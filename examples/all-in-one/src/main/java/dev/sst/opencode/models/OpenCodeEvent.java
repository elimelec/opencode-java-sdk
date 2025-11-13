package dev.sst.opencode.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Server-sent event from OpenCode")
public class OpenCodeEvent {

	@JsonProperty("type")
	@Schema(description = "Event type", example = "session.message.part")
	private String type;

	@JsonProperty("properties")
	@Schema(description = "Event properties")
	private Map<String, Object> properties;

	@JsonProperty("timestamp")
	@Schema(description = "Event timestamp")
	private Long timestamp;

	/**
	 * Check if this is a message event
	 */
	public boolean isMessageEvent() {
		return type != null && type.startsWith("session.message.");
	}

	/**
	 * Check if this is a tool event
	 */
	public boolean isToolEvent() {
		return type != null && type.contains(".tool.");
	}

	/**
	 * Check if this is an error event
	 */
	public boolean isErrorEvent() {
		return type != null && type.contains(".error");
	}

	/**
	 * Get a property value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, Class<T> type) {
		if (properties == null) {
			return null;
		}
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		if (type.isInstance(value)) {
			return (T) value;
		}
		throw new ClassCastException("Property " + key + " is not of type " + type.getName());
	}

}