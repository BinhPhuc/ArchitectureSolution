package com.architecture.solution.repository;

import com.architecture.solution.entity.UserRole;
import com.architecture.solution.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    boolean existsByRoleIdAndIsDeletedFalse(String roleId);

    @Query(value = """
    select r.name
    from user_roles ur
    join roles r on r.id = ur.role_id
    where ur.user_id = :userId
      and ur.is_deleted = false
      and r.is_deleted = false""", nativeQuery = true)
    List<RoleName> findRoleNamesByUserId(String userId);
}
