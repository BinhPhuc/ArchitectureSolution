package com.architecture.solution.repository;

import com.architecture.solution.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
    Optional<Candidate> findByIdAndIsDeletedFalse(String id);
}
