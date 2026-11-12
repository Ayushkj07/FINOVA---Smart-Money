package com.finova.repository;

import com.finova.entity.Role;
import com.finova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    Optional<User> findByVerificationToken(String token);
    boolean existsByRolesContaining(Role role);
    List<User> findByRolesContaining(Role role);
}
