package com.servu.app.auth.bootstrap;

import com.servu.app.auth.domain.AppUser;
import com.servu.app.auth.domain.AppUserRole;
import com.servu.app.auth.persistence.AppUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

	private final AdminBootstrapProperties properties;
	private final AppUserRepository users;
	private final PasswordEncoder passwordEncoder;

	public AdminBootstrapRunner(
		AdminBootstrapProperties properties,
		AppUserRepository users,
		PasswordEncoder passwordEncoder
	) {
		this.properties = properties;
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!StringUtils.hasText(properties.email()) || !StringUtils.hasText(properties.password())) {
			return;
		}

		if (properties.password().length() < 12) {
			throw new IllegalStateException("SERVU_ADMIN_PASSWORD must be at least 12 characters");
		}

		users.findByEmailIgnoreCase(properties.email()).ifPresentOrElse(
			existing -> {
			},
			() -> users.save(new AppUser(
				properties.email().trim().toLowerCase(),
				passwordEncoder.encode(properties.password()),
				displayName(),
				AppUserRole.PLATFORM_ADMIN,
				null
			))
		);
	}

	private String displayName() {
		if (StringUtils.hasText(properties.displayName())) {
			return properties.displayName().trim();
		}
		return "Platform Admin";
	}
}
