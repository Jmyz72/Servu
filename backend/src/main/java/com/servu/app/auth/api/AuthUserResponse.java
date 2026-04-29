package com.servu.app.auth.api;

import com.servu.app.auth.domain.AppUser;
import com.servu.app.auth.domain.AppUserRole;

public record AuthUserResponse(
	Long id,
	String email,
	String displayName,
	AppUserRole role,
	Long vendorId
) {

	public static AuthUserResponse from(AppUser user) {
		return new AuthUserResponse(
			user.getId(),
			user.getEmail(),
			user.getDisplayName(),
			user.getRole(),
			user.getVendorId()
		);
	}
}
