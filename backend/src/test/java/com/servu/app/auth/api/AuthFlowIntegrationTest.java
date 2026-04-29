package com.servu.app.auth.api;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.servu.app.auth.domain.AppUser;
import com.servu.app.auth.domain.AppUserRole;
import com.servu.app.auth.persistence.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:servu-auth;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"servu.admin.email=admin@example.com",
	"servu.admin.password=correct-password",
	"servu.admin.display-name=Test Admin"
})
class AuthFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AppUserRepository users;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void loginSucceedsWithSeededAdminCredentials() throws Exception {
		MvcResult result = login("admin@example.com", "correct-password")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is("admin@example.com")))
			.andExpect(jsonPath("$.displayName", is("Test Admin")))
			.andExpect(jsonPath("$.role", is("PLATFORM_ADMIN")))
			.andExpect(jsonPath("$.vendorId", nullValue()))
			.andReturn();

		MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

		mockMvc.perform(get("/api/auth/me").session(session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is("admin@example.com")));
	}

	@Test
	void loginFailureUsesGenericMessage() throws Exception {
		login("admin@example.com", "wrong-password")
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message", is("Invalid email or password")));

		login("missing@example.com", "wrong-password")
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message", is("Invalid email or password")));
	}

	@Test
	void meRejectsUnauthenticatedRequests() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message", is("Authentication required")));
	}

	@Test
	void logoutInvalidatesSession() throws Exception {
		MvcResult result = login("admin@example.com", "correct-password").andReturn();
		MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

		mockMvc.perform(post("/api/auth/logout").with(csrf()).session(session))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/auth/me").session(session))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void protectedAreasRejectUnauthenticatedRequests() throws Exception {
		mockMvc.perform(get("/api/admin/probe"))
			.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/vendor/probe"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void healthAndCustomerApiRoutesRemainPublic() throws Exception {
		mockMvc.perform(get("/api/health"))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/customer/demo/menu"))
			.andExpect(status().isNotFound());
	}

	@Test
	void vendorScopedUserPreservesVendorContext() throws Exception {
		users.save(new AppUser(
			"staff@example.com",
			passwordEncoder.encode("staff-password"),
			"Vendor Staff",
			AppUserRole.VENDOR_STAFF,
			42L
		));

		login("staff@example.com", "staff-password")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.role", is("VENDOR_STAFF")))
			.andExpect(jsonPath("$.vendorId", is(42)));
	}

	@Test
	void csrfEndpointReturnsHeaderToken() throws Exception {
		mockMvc.perform(get("/api/auth/csrf"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.headerName", is("X-XSRF-TOKEN")));
	}

	private org.springframework.test.web.servlet.ResultActions login(String email, String password) throws Exception {
		return mockMvc.perform(post("/api/auth/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{"email":"%s","password":"%s"}
				""".formatted(email, password)));
	}
}
