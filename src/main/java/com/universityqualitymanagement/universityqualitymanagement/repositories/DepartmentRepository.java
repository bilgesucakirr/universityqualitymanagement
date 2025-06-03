package com.universityqualitymanagement.universityqualitymanagement.repositories;

import com.universityqualitymanagement.universityqualitymanagement.models.Department;
import com.universityqualitymanagement.universityqualitymanagement.models.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Optional<Department> findByNameAndFaculty(String name, Faculty faculty);
}