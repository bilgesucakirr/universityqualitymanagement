package com.universityqualitymanagement.universityqualitymanagement.dtos.university;

// This DTO can be used for both request and response for Faculty data
public class FacultyDto {
    private String id; // For response and update requests
    private String name;

    public FacultyDto() {
    }

    public FacultyDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FacultyDto(String name) {
        this.name = name;
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
}