package com.servu.app.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@NotBlank @Email String email,
	@NotBlank @Size(min = 12, message = "Password must be at least 12 characters") String password
) {
}
