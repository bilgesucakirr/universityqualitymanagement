package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String courseCode; // E.g., INDE2001.1

    @Column(nullable = false)
    private String courseName; // E.g., Operations Research I

    private Integer credits; // E.g., 4

    private String semester; // E.g., FALL23

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id") // Bir dersin bir ana öğretim elemanı olabilir
    private Instructor instructor;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, Integer credits, String semester, Department department, Instructor instructor) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.department = department;
        this.instructor = instructor;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}