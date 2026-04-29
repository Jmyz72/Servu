package com.servu.app.auth.persistence;

import java.util.Optional;

import com.servu.app.auth.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByEmailIgnoreCase(String email);
}
