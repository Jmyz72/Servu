package com.servu.app.common.api;

import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
		MethodArgumentNotValidException exception,
		HttpServletRequest request
	) {
		List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::toFieldErrorResponse)
			.toList();

		ApiErrorResponse response = new ApiErrorResponse(
			Instant.now(),
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			"Request validation failed",
			request.getRequestURI(),
			fieldErrors
		);

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(
		ResponseStatusException exception,
		HttpServletRequest request
	) {
		int status = exception.getStatusCode().value();
		String message = exception.getReason() == null ? "Request failed" : exception.getReason();
		ApiErrorResponse response = ApiErrorResponse.withoutFieldErrors(
			status,
			exception.getStatusCode().toString(),
			message,
			request.getRequestURI()
		);

		return ResponseEntity.status(exception.getStatusCode()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
		ApiErrorResponse response = ApiErrorResponse.withoutFieldErrors(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
			"An unexpected error occurred",
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
		String message = fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage();
		return new FieldErrorResponse(fieldError.getField(), message);
	}
}
