package com.servu.app.auth.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "servu.admin")
public record AdminBootstrapProperties(
	String email,
	String password,
	String displayName
) {
}
