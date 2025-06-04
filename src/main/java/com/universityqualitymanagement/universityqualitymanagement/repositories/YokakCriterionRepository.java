package com.universityqualitymanagement.universityqualitymanagement.repositories;

import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;
import com.universityqualitymanagement.universityqualitymanagement.models.YokakCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YokakCriterionRepository extends JpaRepository<YokakCriterion, String> {
    Optional<YokakCriterion> findByCode(String code);
    boolean existsByCode(String code);
    List<YokakCriterion> findByLevel(CriterionLevel level);
    List<YokakCriterion> findByParent(YokakCriterion parent);
    List<YokakCriterion> findByParentIsNullAndLevel(CriterionLevel level);

    // NEW: Search by code or name (case-insensitive)
    List<YokakCriterion> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String codeSearchTerm, String nameSearchTerm);

    // NEW: Search by code or name within a specific level
    List<YokakCriterion> findByLevelAndCodeContainingIgnoreCaseOrLevelAndNameContainingIgnoreCase(CriterionLevel level1, String codeSearchTerm, CriterionLevel level2, String nameSearchTerm);

    // NEW: Search by code or name within children of a specific parent
    List<YokakCriterion> findByParentAndCodeContainingIgnoreCaseOrParentAndNameContainingIgnoreCase(YokakCriterion parent, String codeSearchTerm, YokakCriterion parent2, String nameSearchTerm);
}