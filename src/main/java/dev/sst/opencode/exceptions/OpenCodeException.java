package dev.sst.opencode.exceptions;

/**
 * Base exception for all OpenCode SDK errors
 */
public class OpenCodeException extends RuntimeException {

	private final int statusCode;

	private final String errorCode;

	public OpenCodeException(String message) {
		this(message, 0, null);
	}

	public OpenCodeException(String message, Throwable cause) {
		this(message, 0, null, cause);
	}

	public OpenCodeException(String message, int statusCode, String errorCode) {
		super(message);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
	}

	public OpenCodeException(String message, int statusCode, String errorCode, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	// Specific exception types

	public static class NotFound extends OpenCodeException {

		public NotFound(String message) {
			super(message, 404, "NOT_FOUND");
		}

	}

	public static class Unauthorized extends OpenCodeException {

		public Unauthorized(String message) {
			super(message, 401, "UNAUTHORIZED");
		}

	}

	public static class BadRequest extends OpenCodeException {

		public BadRequest(String message) {
			super(message, 400, "BAD_REQUEST");
		}

	}

	public static class ServerError extends OpenCodeException {

		public ServerError(String message) {
			super(message, 500, "SERVER_ERROR");
		}

	}

	public static class NetworkError extends OpenCodeException {

		public NetworkError(String message, Throwable cause) {
			super(message, 0, "NETWORK_ERROR", cause);
		}

	}

}