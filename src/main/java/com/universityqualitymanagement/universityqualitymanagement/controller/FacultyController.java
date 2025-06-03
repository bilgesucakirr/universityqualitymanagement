package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.FacultyDto;
import com.universityqualitymanagement.universityqualitymanagement.services.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // Create a new faculty
    @PostMapping
    public ResponseEntity<FacultyDto> createFaculty(@RequestBody FacultyDto facultyDto) {
        FacultyDto createdFaculty = facultyService.createFaculty(facultyDto);
        return new ResponseEntity<>(createdFaculty, HttpStatus.CREATED);
    }

    // Get all faculties
    @GetMapping
    public ResponseEntity<List<FacultyDto>> getAllFaculties() {
        List<FacultyDto> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    // Get faculty by ID
    @GetMapping("/{id}")
    public ResponseEntity<FacultyDto> getFacultyById(@PathVariable String id) {
        FacultyDto faculty = facultyService.getFacultyById(id);
        return ResponseEntity.ok(faculty);
    }

    // Update an existing faculty
    @PutMapping("/{id}")
    public ResponseEntity<FacultyDto> updateFaculty(@PathVariable String id, @RequestBody FacultyDto facultyDto) {
        FacultyDto updatedFaculty = facultyService.updateFaculty(id, facultyDto);
        return ResponseEntity.ok(updatedFaculty);
    }

    // Delete a faculty by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable String id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    // --- IMPORTANT: Add authorization (e.g., @PreAuthorize("hasRole('ADMIN')")) later for security. ---
}