package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

import java.util.List;

public class CreateSurveyRequest {
    private String title;
    private String description;
    private List<String> questionTexts; // Sadece soru metinleri alacağız

    public CreateSurveyRequest() {
    }

    public CreateSurveyRequest(String title, String description, List<String> questionTexts) {
        this.title = title;
        this.description = description;
        this.questionTexts = questionTexts;
    }

    // Getters and Setters
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

    public List<String> getQuestionTexts() {
        return questionTexts;
    }

    public void setQuestionTexts(List<String> questionTexts) {
        this.questionTexts = questionTexts;
    }
}