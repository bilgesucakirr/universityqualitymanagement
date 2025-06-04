package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.university.CourseDto;
import com.universityqualitymanagement.universityqualitymanagement.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto) {
        // SECURITY NOTE: Unprotected endpoint. Admin access required in a real app.
        CourseDto createdCourse = courseService.createCourse(courseDto);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam(required = false) String departmentId) {
        // SECURITY NOTE: Unprotected endpoint.
        List<CourseDto> courses;
        if (departmentId != null && !departmentId.isEmpty()) {
            courses = courseService.getCoursesByDepartmentId(departmentId);
        } else {
            courses = courseService.getAllCourses();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        CourseDto course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable String id, @RequestBody CourseDto courseDto) {
        // SECURITY NOTE: Unprotected endpoint.
        CourseDto updatedCourse = courseService.updateCourse(id, courseDto);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint.
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}