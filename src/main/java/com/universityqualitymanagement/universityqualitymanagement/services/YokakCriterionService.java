package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.CreateYokakCriterionRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.UpdateYokakCriterionRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.YokakCriterionResponse;
import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;
import com.universityqualitymanagement.universityqualitymanagement.models.YokakCriterion;
import com.universityqualitymanagement.universityqualitymanagement.repositories.YokakCriterionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YokakCriterionService {

    private final YokakCriterionRepository yokakCriterionRepository;

    @Autowired
    public YokakCriterionService(YokakCriterionRepository yokakCriterionRepository) {
        this.yokakCriterionRepository = yokakCriterionRepository;
    }

    @Transactional
    public YokakCriterionResponse createYokakCriterion(CreateYokakCriterionRequest request) {
        if (yokakCriterionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("YÖKAK Criterion with code " + request.getCode() + " already exists.");
        }

        YokakCriterion parent = null;
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            parent = yokakCriterionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent YÖKAK Criterion not found with ID: " + request.getParentId()));

            if (request.getLevel() == CriterionLevel.MAIN_CRITERION && parent.getLevel() != CriterionLevel.HEADER) {
                throw new IllegalArgumentException("Main Criterion must have a Header as parent.");
            }
            if (request.getLevel() == CriterionLevel.SUB_CRITERION && parent.getLevel() != CriterionLevel.MAIN_CRITERION) {
                throw new IllegalArgumentException("Sub Criterion must have a Main Criterion as parent.");
            }
        } else if (request.getLevel() != CriterionLevel.HEADER) {
            throw new IllegalArgumentException("Non-Header criteria must have a parent.");
        }

        YokakCriterion newCriterion = new YokakCriterion(request.getCode(), request.getName(), request.getLevel(), parent);
        YokakCriterion savedCriterion = yokakCriterionRepository.save(newCriterion);
        return mapToResponse(savedCriterion);
    }

    // UPDATED: Now accepts searchTerm. This method will handle all filtering combinations.
    public List<YokakCriterionResponse> getAllYokakCriteria(String level, String parentId, String searchTerm) {
        List<YokakCriterion> criteria;

        // Determine the filtering logic based on provided parameters
        if (searchTerm != null && !searchTerm.isEmpty()) {
            if (level != null && !level.isEmpty()) {
                CriterionLevel criterionLevel = CriterionLevel.valueOf(level.toUpperCase());
                criteria = yokakCriterionRepository.findByLevelAndCodeContainingIgnoreCaseOrLevelAndNameContainingIgnoreCase(
                        criterionLevel, searchTerm, criterionLevel, searchTerm
                );
            } else if (parentId != null && !parentId.isEmpty()) {
                YokakCriterion parent = yokakCriterionRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException("Parent YÖKAK Criterion not found with ID: " + parentId));
                criteria = yokakCriterionRepository.findByParentAndCodeContainingIgnoreCaseOrParentAndNameContainingIgnoreCase(
                        parent, searchTerm, parent, searchTerm
                );
            } else {
                // If only searchTerm is present, search across all criteria
                criteria = yokakCriterionRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                        searchTerm, searchTerm
                );
            }
        } else {
            // If no searchTerm, fallback to original level/parentId filtering
            if (level != null && !level.isEmpty()) {
                CriterionLevel criterionLevel = CriterionLevel.valueOf(level.toUpperCase());
                criteria = yokakCriterionRepository.findByLevel(criterionLevel);
            } else if (parentId != null && !parentId.isEmpty()) {
                YokakCriterion parent = yokakCriterionRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException("Parent YÖKAK Criterion not found with ID: " + parentId));
                criteria = yokakCriterionRepository.findByParent(parent);
            } else {
                criteria = yokakCriterionRepository.findAll(); // No filters, return all
            }
        }

        return criteria.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Removed specific getYokakCriteriaByLevel and getChildrenOfCriterion public methods
    // as getAllYokakCriteria now handles them.
    // However, if internal logic needs them, keep them private or rename.
    // For dropdown population, we still need methods that only fetch by level without search term.
    public List<YokakCriterionResponse> getYokakCriteriaByLevelForDropdowns(CriterionLevel level) {
        return yokakCriterionRepository.findByLevel(level).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public YokakCriterionResponse getYokakCriterionById(String id) {
        return yokakCriterionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("YÖKAK Criterion not found with ID: " + id));
    }

    @Transactional
    public YokakCriterionResponse updateYokakCriterion(String id, UpdateYokakCriterionRequest request) {
        YokakCriterion existingCriterion = yokakCriterionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("YÖKAK Criterion not found with ID: " + id));

        if (request.getCode() != null && !request.getCode().equals(existingCriterion.getCode())) {
            if (yokakCriterionRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("YÖKAK Criterion with code " + request.getCode() + " already exists.");
            }
            existingCriterion.setCode(request.getCode());
        }

        if (request.getName() != null) {
            existingCriterion.setName(request.getName());
        }

        if (request.getLevel() != null && request.getLevel() != existingCriterion.getLevel()) {
            // Additional validation could be here
            existingCriterion.setLevel(request.getLevel());
        }

        if (request.getParentId() != null) {
            if (!request.getParentId().isEmpty()) {
                YokakCriterion newParent = yokakCriterionRepository.findById(request.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("New Parent YÖKAK Criterion not found with ID: " + request.getParentId()));
                existingCriterion.setParent(newParent);

                if (existingCriterion.getLevel() == CriterionLevel.MAIN_CRITERION && newParent.getLevel() != CriterionLevel.HEADER) {
                    throw new IllegalArgumentException("Main Criterion must have a Header as parent.");
                }
                if (existingCriterion.getLevel() == CriterionLevel.SUB_CRITERION && newParent.getLevel() != CriterionLevel.MAIN_CRITERION) {
                    throw new IllegalArgumentException("Sub Criterion must have a Main Criterion as parent.");
                }
            } else {
                if (existingCriterion.getLevel() != CriterionLevel.HEADER) {
                    throw new IllegalArgumentException("Only Header criteria can have no parent.");
                }
                existingCriterion.setParent(null);
            }
        } else if (existingCriterion.getParent() != null && existingCriterion.getLevel() != CriterionLevel.HEADER) {
            // If request.getParentId() is null, and it's not a Header, keep current parent
        }


        YokakCriterion updatedCriterion = yokakCriterionRepository.save(existingCriterion);
        return mapToResponse(updatedCriterion);
    }

    @Transactional
    public void deleteYokakCriterion(String id) {
        if (!yokakCriterionRepository.existsById(id)) {
            throw new IllegalArgumentException("YÖKAK Criterion not found with ID: " + id);
        }
        yokakCriterionRepository.deleteById(id);
    }

    private YokakCriterionResponse mapToResponse(YokakCriterion criterion) {
        String parentId = criterion.getParent() != null ? criterion.getParent().getId() : null;
        String parentCode = criterion.getParent() != null ? criterion.getParent().getCode() : null;
        String parentName = criterion.getParent() != null ? criterion.getParent().getName() : null;

        return new YokakCriterionResponse(
                criterion.getId(),
                criterion.getCode(),
                criterion.getName(),
                criterion.getLevel(),
                parentId,
                parentCode,
                parentName
        );
    }
}