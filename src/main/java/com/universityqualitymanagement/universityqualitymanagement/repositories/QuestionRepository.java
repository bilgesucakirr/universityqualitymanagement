package com.universityqualitymanagement.universityqualitymanagement.repositories;

import com.universityqualitymanagement.universityqualitymanagement.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
}