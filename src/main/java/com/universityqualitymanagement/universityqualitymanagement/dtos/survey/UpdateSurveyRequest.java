package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

import java.util.List;

public class UpdateSurveyRequest {
    private String title;
    private String description;
    private List<UpdateQuestionRequest> questions;

    public UpdateSurveyRequest() {}

    public UpdateSurveyRequest(String title, String description, List<UpdateQuestionRequest> questions) {
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<UpdateQuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<UpdateQuestionRequest> questions) { this.questions = questions; }
}