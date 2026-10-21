package com.architecture.solution.repository;

import com.architecture.solution.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByUsernameAndIsDeletedFalse(String username);
    
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByRefreshTokenAndIsDeletedFalse(String refreshToken);
}
