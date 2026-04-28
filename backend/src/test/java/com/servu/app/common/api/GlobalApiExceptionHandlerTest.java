package com.servu.app.common.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

class GlobalApiExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
			.setControllerAdvice(new GlobalApiExceptionHandler())
			.build();
	}

	@Test
	void formatsResponseStatusExceptions() throws Exception {
		mockMvc.perform(get("/api/test/not-found"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Missing test resource")))
			.andExpect(jsonPath("$.path", is("/api/test/not-found")))
			.andExpect(jsonPath("$.fieldErrors", hasSize(0)));
	}

	@Test
	void formatsValidationErrors() throws Exception {
		mockMvc.perform(post("/api/test/validate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("Request validation failed")))
			.andExpect(jsonPath("$.fieldErrors", hasSize(1)))
			.andExpect(jsonPath("$.fieldErrors[0].field", is("name")));
	}

	@RestController
	public static class TestController {

		@GetMapping("/api/test/not-found")
		void notFound() {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing test resource");
		}

		@PostMapping("/api/test/validate")
		void validate(@Valid @RequestBody TestRequest request) {
		}
	}

	public record TestRequest(@NotBlank String name) {
	}
}
