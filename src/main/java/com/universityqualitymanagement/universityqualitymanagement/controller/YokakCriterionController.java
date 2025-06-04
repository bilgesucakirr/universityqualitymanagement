package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.CreateYokakCriterionRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.UpdateYokakCriterionRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.yokak.YokakCriterionResponse;
import com.universityqualitymanagement.universityqualitymanagement.models.CriterionLevel;
import com.universityqualitymanagement.universityqualitymanagement.services.YokakCriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yokak-criteria")
public class YokakCriterionController {

    private final YokakCriterionService yokakCriterionService;

    @Autowired
    public YokakCriterionController(YokakCriterionService yokakCriterionService) {
        this.yokakCriterionService = yokakCriterionService;
    }

    @PostMapping
    public ResponseEntity<YokakCriterionResponse> createYokakCriterion(@RequestBody CreateYokakCriterionRequest request) {
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        YokakCriterionResponse createdCriterion = yokakCriterionService.createYokakCriterion(request);
        return new ResponseEntity<>(createdCriterion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<YokakCriterionResponse>> getAllYokakCriteria(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String searchTerm) { // NEW: searchTerm parameter
        // SECURITY NOTE: Unprotected endpoint.
        List<YokakCriterionResponse> criteria;
        // Use the updated service method that handles all filtering and searching
        criteria = yokakCriterionService.getAllYokakCriteria(level, parentId, searchTerm);
        return ResponseEntity.ok(criteria);
    }

    // NEW: Endpoint to get criteria by level, specifically for dropdown population
    // This is needed because the main /yokak-criteria GET now includes search and combined filters
    @GetMapping("/by-level")
    public ResponseEntity<List<YokakCriterionResponse>> getYokakCriteriaForDropdowns(@RequestParam String level) {
        try {
            CriterionLevel criterionLevel = CriterionLevel.valueOf(level.toUpperCase());
            List<YokakCriterionResponse> criteria = yokakCriterionService.getYokakCriteriaByLevelForDropdowns(criterionLevel);
            return ResponseEntity.ok(criteria);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid level provided
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<YokakCriterionResponse> getYokakCriterionById(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        YokakCriterionResponse criterion = yokakCriterionService.getYokakCriterionById(id);
        return ResponseEntity.ok(criterion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<YokakCriterionResponse> updateYokakCriterion(@PathVariable String id, @RequestBody UpdateYokakCriterionRequest request) {
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        YokakCriterionResponse updatedCriterion = yokakCriterionService.updateYokakCriterion(id, request);
        return ResponseEntity.ok(updatedCriterion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYokakCriterion(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        yokakCriterionService.deleteYokakCriterion(id);
        return ResponseEntity.noContent().build();
    }
}