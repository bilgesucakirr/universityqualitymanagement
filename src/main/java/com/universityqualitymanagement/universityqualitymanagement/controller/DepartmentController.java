package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.DepartmentDto;
import com.universityqualitymanagement.universityqualitymanagement.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // Create a new department
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    // Get all departments
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    // Get department by ID
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable String id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    // Update an existing department
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable String id, @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }

    // Delete a department by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    // --- IMPORTANT: Add authorization (e.g., @PreAuthorize("hasRole('ADMIN')")) later for security. ---
}