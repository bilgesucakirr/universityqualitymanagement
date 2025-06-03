package com.universityqualitymanagement.universityqualitymanagement.dtos.response;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role; // Rol adını doğrudan DTO'ya ekleyelim
    private String facultyId;
    private String facultyName;
    private String departmentId;
    private String departmentName;


    public UserResponse(String id, String name, String email, String role, String facultyId, String facultyName, String departmentId, String departmentName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}