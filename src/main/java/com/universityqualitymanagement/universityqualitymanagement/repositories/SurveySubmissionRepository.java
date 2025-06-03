package com.universityqualitymanagement.universityqualitymanagement.repositories;

import com.universityqualitymanagement.universityqualitymanagement.models.SurveySubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, String> {
    boolean existsBySubmissionCode(String submissionCode);
}