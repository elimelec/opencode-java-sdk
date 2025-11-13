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
@Schema(description = "Permission request")
public class Permission {

	@JsonProperty("id")
	@Schema(description = "Permission request ID")
	private String id;

	@JsonProperty("type")
	@Schema(description = "Permission type", example = "file_write")
	private String type;

	@JsonProperty("resource")
	@Schema(description = "Resource being accessed")
	private String resource;

	@JsonProperty("action")
	@Schema(description = "Action being performed")
	private String action;

	@JsonProperty("description")
	@Schema(description = "Human-readable description")
	private String description;

	@JsonProperty("metadata")
	@Schema(description = "Additional metadata")
	private Map<String, Object> metadata;

	@JsonProperty("status")
	@Schema(description = "Permission status (pending, granted, denied)")
	private String status;

	@JsonProperty("timestamp")
	@Schema(description = "Request timestamp")
	private Long timestamp;

}