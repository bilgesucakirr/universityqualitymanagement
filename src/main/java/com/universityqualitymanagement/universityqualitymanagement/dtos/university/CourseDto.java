package com.universityqualitymanagement.universityqualitymanagement.dtos.university;

public class CourseDto {
    private String id;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String semester;
    private String departmentId; // Mandatory for linking course to department
    private String departmentName; // For response
    private String instructorId; // Optional, for linking to an instructor
    private String instructorName; // For response

    public CourseDto() {}

    public CourseDto(String courseCode, String courseName, Integer credits, String semester, String departmentId, String instructorId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.departmentId = departmentId;
        this.instructorId = instructorId;
    }

    // Constructor for response (with all info)
    public CourseDto(String id, String courseCode, String courseName, Integer credits, String semester, String departmentId, String departmentName, String instructorId, String instructorName) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
}