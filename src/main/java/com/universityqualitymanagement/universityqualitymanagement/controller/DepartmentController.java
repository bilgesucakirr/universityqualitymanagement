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

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        // SECURITY NOTE: Unprotected endpoint. Admin access required in a real app.
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments(@RequestParam(required = false) String facultyId) {
        // SECURITY NOTE: Unprotected endpoint.
        List<DepartmentDto> departments;
        if (facultyId != null && !facultyId.isEmpty()) {
            departments = departmentService.getDepartmentsByFacultyId(facultyId);
        } else {
            departments = departmentService.getAllDepartments();
        }
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable String id, @RequestBody DepartmentDto departmentDto) {
        // SECURITY NOTE: Unprotected endpoint.
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}