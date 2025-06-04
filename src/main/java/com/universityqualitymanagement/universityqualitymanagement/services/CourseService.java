package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.CourseDto;
import com.universityqualitymanagement.universityqualitymanagement.models.Course;
import com.universityqualitymanagement.universityqualitymanagement.models.Department;
import com.universityqualitymanagement.universityqualitymanagement.models.Instructor;
import com.universityqualitymanagement.universityqualitymanagement.repositories.CourseRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.DepartmentRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final InstructorRepository instructorRepository; // Assuming you might have instructors

    @Autowired
    public CourseService(CourseRepository courseRepository, DepartmentRepository departmentRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public CourseDto createCourse(CourseDto courseDto) {
        Department department = departmentRepository.findById(courseDto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + courseDto.getDepartmentId()));

        // Check for duplicate course code within the same semester and department
        if (courseRepository.findByCourseCodeAndSemesterAndDepartment(courseDto.getCourseCode(), courseDto.getSemester(), department).isPresent()) {
            throw new IllegalArgumentException("Course with code " + courseDto.getCourseCode() + " and semester " + courseDto.getSemester() + " already exists in this department.");
        }

        Instructor instructor = null;
        if (courseDto.getInstructorId() != null && !courseDto.getInstructorId().isEmpty()) {
            instructor = instructorRepository.findById(courseDto.getInstructorId())
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + courseDto.getInstructorId()));
        }

        Course course = new Course(courseDto.getCourseCode(), courseDto.getCourseName(), courseDto.getCredits(), courseDto.getSemester(), department, instructor);
        Course savedCourse = courseRepository.save(course);
        return mapToCourseDto(savedCourse);
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    // Optional: Get courses by department ID
    public List<CourseDto> getCoursesByDepartmentId(String departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + departmentId));
        return courseRepository.findByDepartment(department).stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    public CourseDto getCourseById(String id) {
        return courseRepository.findById(id)
                .map(this::mapToCourseDto)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));
    }

    @Transactional
    public CourseDto updateCourse(String id, CourseDto courseDto) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));

        // Update fields if provided in DTO
        if (courseDto.getCourseCode() != null) existingCourse.setCourseCode(courseDto.getCourseCode());
        if (courseDto.getCourseName() != null) existingCourse.setCourseName(courseDto.getCourseName());
        if (courseDto.getCredits() != null) existingCourse.setCredits(courseDto.getCredits());
        if (courseDto.getSemester() != null) existingCourse.setSemester(courseDto.getSemester());

        if (courseDto.getDepartmentId() != null && !courseDto.getDepartmentId().equals(existingCourse.getDepartment().getId())) {
            Department newDepartment = departmentRepository.findById(courseDto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("New Department not found with ID: " + courseDto.getDepartmentId()));
            existingCourse.setDepartment(newDepartment);
        }

        if (courseDto.getInstructorId() != null && (existingCourse.getInstructor() == null || !courseDto.getInstructorId().equals(existingCourse.getInstructor().getId()))) {
            Instructor newInstructor = instructorRepository.findById(courseDto.getInstructorId())
                    .orElseThrow(() -> new IllegalArgumentException("New Instructor not found with ID: " + courseDto.getInstructorId()));
            existingCourse.setInstructor(newInstructor);
        } else if (courseDto.getInstructorId() == null) {
            existingCourse.setInstructor(null); // Allow setting instructor to null
        }



        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToCourseDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(String id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found with ID: " + id);
        }

        courseRepository.deleteById(id);
    }

    private CourseDto mapToCourseDto(Course course) {
        String departmentId = course.getDepartment() != null ? course.getDepartment().getId() : null;
        String departmentName = course.getDepartment() != null ? course.getDepartment().getName() : null;
        String instructorId = course.getInstructor() != null ? course.getInstructor().getId() : null;
        String instructorName = course.getInstructor() != null ? course.getInstructor().getName() : null;

        return new CourseDto(
                course.getId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getSemester(),
                departmentId,
                departmentName,
                instructorId,
                instructorName
        );
    }
}