package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE  u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    @Query("SELECT u FROM User u WHERE u.isActive = true and u.username = :username")
    Optional<User> findActiveByUsername(@Param("username") String username);

    Optional<User> findByVerificationToken(@Param("verificationToken") String verificationToken);

    List<User> getAllUser();
}
