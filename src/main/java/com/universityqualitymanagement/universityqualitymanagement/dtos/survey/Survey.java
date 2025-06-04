// src/main/java/com/universityqualitymanagement/universityqualitymanagement/dtos/survey/Survey.java
package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

import java.util.List;

public class Survey {
    private String id;
    private String title;
    private String description;
    private List<QuestionResponse> questions; // Added questions list

    public Survey() {
    }

    // Existing constructor
    public Survey(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // New constructor including questions list, for response DTO
    public Survey(String id, String title, String description, List<QuestionResponse> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}