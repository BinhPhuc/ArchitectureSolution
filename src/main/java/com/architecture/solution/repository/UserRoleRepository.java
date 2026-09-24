package com.architecture.solution.repository;

import com.architecture.solution.entity.UserRole;
import com.architecture.solution.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    boolean existsByRoleIdAndIsDeletedFalse(String roleId);

    @Query("""
            select r.name
            from UserRole ur
            join Role r on r.id = ur.roleId
            where ur.userId = :userId
              and ur.isDeleted = false
              and r.isDeleted = false""")
    List<RoleName> findRoleNamesByUserId(@Param("userId") String userId);
}
