package dev.sst.opencode.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * JSON serialization/deserialization utilities
 */
public class JsonUtils {

	private static final ObjectMapper MAPPER = createMapper();

	private static ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static String toJson(Object obj) {
		try {
			return MAPPER.writeValueAsString(obj);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to serialize object to JSON", e);
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to deserialize JSON to " + clazz.getName(), e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> typeRef) {
		try {
			return MAPPER.readValue(json, typeRef);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to deserialize JSON", e);
		}
	}

	public static ObjectMapper getMapper() {
		return MAPPER;
	}

}