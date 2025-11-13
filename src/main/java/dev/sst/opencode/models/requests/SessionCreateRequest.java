package dev.sst.opencode.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request to create a new session")
public class SessionCreateRequest {

	@JsonProperty("parentID")
	@Schema(description = "Parent session ID", required = false)
	private String parentId;

	@JsonProperty("title")
	@Schema(description = "Session title", required = false)
	@Size(max = 255)
	private String title;

}