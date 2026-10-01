package com.architecture.solution.repository;

import com.architecture.solution.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, String> {
    Optional<Recruiter> findById(String id);
}
