package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.DepartmentDto;
import com.universityqualitymanagement.universityqualitymanagement.models.Department;
import com.universityqualitymanagement.universityqualitymanagement.models.Faculty;
import com.universityqualitymanagement.universityqualitymanagement.repositories.DepartmentRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository; // Needed to find Faculty by ID

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, FacultyRepository facultyRepository) {
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
    }

    // Create a new department
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Faculty faculty = facultyRepository.findById(departmentDto.getFacultyId())
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + departmentDto.getFacultyId()));

        if (departmentRepository.findByNameAndFaculty(departmentDto.getName(), faculty).isPresent()) {
            throw new IllegalArgumentException("Department with this name already exists in this faculty: " + departmentDto.getName());
        }

        Department department = new Department(departmentDto.getName(), faculty);
        Department savedDepartment = departmentRepository.save(department);
        return new DepartmentDto(savedDepartment.getId(), savedDepartment.getName(), savedDepartment.getFaculty().getId(), savedDepartment.getFaculty().getName());
    }

    // Get all departments
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> new DepartmentDto(
                        department.getId(),
                        department.getName(),
                        department.getFaculty() != null ? department.getFaculty().getId() : null,
                        department.getFaculty() != null ? department.getFaculty().getName() : null
                ))
                .collect(Collectors.toList());
    }

    // Get department by ID
    public DepartmentDto getDepartmentById(String id) {
        return departmentRepository.findById(id)
                .map(department -> new DepartmentDto(
                        department.getId(),
                        department.getName(),
                        department.getFaculty() != null ? department.getFaculty().getId() : null,
                        department.getFaculty() != null ? department.getFaculty().getName() : null
                ))
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));
    }

    // Update an existing department
    public DepartmentDto updateDepartment(String id, DepartmentDto departmentDto) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));

        Faculty newFaculty = null;
        if (departmentDto.getFacultyId() != null && !departmentDto.getFacultyId().equals(existingDepartment.getFaculty().getId())) {
            newFaculty = facultyRepository.findById(departmentDto.getFacultyId())
                    .orElseThrow(() -> new IllegalArgumentException("New Faculty not found with ID: " + departmentDto.getFacultyId()));
            existingDepartment.setFaculty(newFaculty);
        } else {
            newFaculty = existingDepartment.getFaculty(); // Keep existing faculty if not changed
        }

        if (departmentDto.getName() != null && !departmentDto.getName().equals(existingDepartment.getName())) {
            // Check if new name already exists for another department in the same (or new) faculty
            if (departmentRepository.findByNameAndFaculty(departmentDto.getName(), newFaculty).isPresent()) {
                throw new IllegalArgumentException("Another department with this name already exists in this faculty: " + departmentDto.getName());
            }
            existingDepartment.setName(departmentDto.getName());
        }

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return new DepartmentDto(updatedDepartment.getId(), updatedDepartment.getName(), updatedDepartment.getFaculty().getId(), updatedDepartment.getFaculty().getName());
    }

    // Delete a department by ID
    public void deleteDepartment(String id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Department not found with ID: " + id);
        }
        // Consider handling associated courses/users/submissions before deleting
        departmentRepository.deleteById(id);
    }
}