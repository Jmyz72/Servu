package com.servu.app.auth.api;

public record CsrfTokenResponse(String headerName, String token) {
}
