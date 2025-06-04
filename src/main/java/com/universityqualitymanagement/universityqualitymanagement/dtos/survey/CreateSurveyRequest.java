package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

import java.util.List;

public class CreateSurveyRequest {
    private String title;
    private String description;
    private List<CreateQuestionRequest> questions; // Changed from List<String> to List<CreateQuestionDto>

    public CreateSurveyRequest() {}

    public CreateSurveyRequest(String title, String description, List<CreateQuestionRequest> questions) {
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<CreateQuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<CreateQuestionRequest> questions) { this.questions = questions; }
}