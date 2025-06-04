package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

// This DTO is used when creating a new survey, to represent a single question
public class CreateQuestionRequest {
    private String questionText;
    private String yokakCriterionId; // ID of the YÖKAK criterion for this question

    public CreateQuestionRequest() {}

    public CreateQuestionRequest(String questionText, String yokakCriterionId) {
        this.questionText = questionText;
        this.yokakCriterionId = yokakCriterionId;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getYokakCriterionId() {
        return yokakCriterionId;
    }

    public void setYokakCriterionId(String yokakCriterionId) {
        this.yokakCriterionId = yokakCriterionId;
    }
}