package com.universityqualitymanagement.universityqualitymanagement.repositories;

import com.universityqualitymanagement.universityqualitymanagement.models.Course;
import com.universityqualitymanagement.universityqualitymanagement.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCourseCodeAndSemesterAndDepartment(String courseCode, String semester, Department department);
}