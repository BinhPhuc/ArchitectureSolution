package com.architecture.solution.repository;

import com.architecture.solution.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    
    Optional<User> findByUsername(String username);
}
