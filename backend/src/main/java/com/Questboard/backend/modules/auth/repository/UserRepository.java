package com.Questboard.backend.modules.auth.repository;

import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    Optional<User> findByEmail(String email);
}
