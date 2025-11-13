package dev.sst.opencode.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Permission response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionResponse {

	@JsonProperty("granted")
	@Schema(description = "Whether permission is granted", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private boolean granted;

	@JsonProperty("reason")
	@Schema(description = "Reason for the decision")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String reason;

	@JsonProperty("remember")
	@Schema(description = "Remember this decision for future requests")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private boolean remember;

}