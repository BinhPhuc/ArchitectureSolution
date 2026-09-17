package com.architecture.solution.repository;

import com.architecture.solution.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    boolean existsByRoleIdAndIsDeletedFalse(String roleId);
}
