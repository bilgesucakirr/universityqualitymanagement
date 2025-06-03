package com.universityqualitymanagement.universityqualitymanagement.dtos.university;

// This DTO can be used for both request and response for Department data
public class DepartmentDto {
    private String id; // For response and update requests
    private String name;
    private String facultyId; // Mandatory for linking department to faculty
    private String facultyName; // For response, to display faculty name

    public DepartmentDto() {
    }

    // Constructor for creating/updating (when id is not yet known or not needed)
    public DepartmentDto(String name, String facultyId) {
        this.name = name;
        this.facultyId = facultyId;
    }

    // Constructor for response (when all info is available)
    public DepartmentDto(String id, String name, String facultyId, String facultyName) {
        this.id = id;
        this.name = name;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}