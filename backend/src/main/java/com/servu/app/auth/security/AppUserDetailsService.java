package com.servu.app.auth.security;

import com.servu.app.auth.persistence.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository users;

	public AppUserDetailsService(AppUserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return users.findByEmailIgnoreCase(username)
			.map(AppUserPrincipal::new)
			.orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
	}
}
