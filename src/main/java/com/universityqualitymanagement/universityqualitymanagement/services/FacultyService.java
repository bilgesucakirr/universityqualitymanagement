package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.FacultyDto;
import com.universityqualitymanagement.universityqualitymanagement.models.Faculty;
import com.universityqualitymanagement.universityqualitymanagement.repositories.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    // Create a new faculty
    public FacultyDto createFaculty(FacultyDto facultyDto) {
        if (facultyRepository.findByName(facultyDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Faculty with this name already exists: " + facultyDto.getName());
        }
        Faculty faculty = new Faculty(facultyDto.getName());
        Faculty savedFaculty = facultyRepository.save(faculty);
        return new FacultyDto(savedFaculty.getId(), savedFaculty.getName());
    }

    // Get all faculties
    public List<FacultyDto> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(faculty -> new FacultyDto(faculty.getId(), faculty.getName()))
                .collect(Collectors.toList());
    }

    // Get faculty by ID
    public FacultyDto getFacultyById(String id) {
        return facultyRepository.findById(id)
                .map(faculty -> new FacultyDto(faculty.getId(), faculty.getName()))
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));
    }

    // Update an existing faculty
    public FacultyDto updateFaculty(String id, FacultyDto facultyDto) {
        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));

        // Check if new name already exists for another faculty
        if (facultyDto.getName() != null && !facultyDto.getName().equals(existingFaculty.getName())) {
            if (facultyRepository.findByName(facultyDto.getName()).isPresent()) {
                throw new IllegalArgumentException("Another faculty with this name already exists: " + facultyDto.getName());
            }
        }

        if (facultyDto.getName() != null) {
            existingFaculty.setName(facultyDto.getName());
        }
        Faculty updatedFaculty = facultyRepository.save(existingFaculty);
        return new FacultyDto(updatedFaculty.getId(), updatedFaculty.getName());
    }

    // Delete a faculty by ID
    public void deleteFaculty(String id) {
        if (!facultyRepository.existsById(id)) {
            throw new IllegalArgumentException("Faculty not found with ID: " + id);
        }
        // Consider handling associated departments/users/courses/submissions before deleting
        // For simplicity, for now, if there are foreign key constraints, it will throw an exception.
        facultyRepository.deleteById(id);
    }
}