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

    @PostMapping
    public ResponseEntity<FacultyDto> createFaculty(@RequestBody FacultyDto facultyDto) {
        // SECURITY NOTE: Unprotected endpoint. Admin access required in a real app.
        FacultyDto createdFaculty = facultyService.createFaculty(facultyDto);
        return new ResponseEntity<>(createdFaculty, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FacultyDto>> getAllFaculties() {
        // SECURITY NOTE: Unprotected endpoint.
        List<FacultyDto> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyDto> getFacultyById(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        FacultyDto faculty = facultyService.getFacultyById(id);
        return ResponseEntity.ok(faculty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyDto> updateFaculty(@PathVariable String id, @RequestBody FacultyDto facultyDto) {
        // SECURITY NOTE: Unprotected endpoint.
        FacultyDto updatedFaculty = facultyService.updateFaculty(id, facultyDto);
        return ResponseEntity.ok(updatedFaculty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}