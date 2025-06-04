package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

// This DTO is used when updating an existing survey, to represent a single question
public class UpdateQuestionRequest {
    private String id; // Optional: ID is needed if an existing question is being updated, not if it's new within an update request
    private String questionText;
    private String yokakCriterionId;

    public UpdateQuestionRequest() {}

    public UpdateQuestionRequest(String id, String questionText, String yokakCriterionId) {
        this.id = id;
        this.questionText = questionText;
        this.yokakCriterionId = yokakCriterionId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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