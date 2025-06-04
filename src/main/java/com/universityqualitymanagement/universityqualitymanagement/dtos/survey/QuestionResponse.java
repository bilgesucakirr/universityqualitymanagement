package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

public class QuestionResponse {
    private String id;
    private String questionText;
    private String questionType;
    private String yokakCriterionId;
    private String yokakCriterionCode;
    private String yokakCriterionName;

    public QuestionResponse() {}

    public QuestionResponse(String id, String questionText, String questionType, String yokakCriterionId, String yokakCriterionCode, String yokakCriterionName) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
        this.yokakCriterionId = yokakCriterionId;
        this.yokakCriterionCode = yokakCriterionCode;
        this.yokakCriterionName = yokakCriterionName;
    }

    // Getters and Setters (existing ones remain)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public String getYokakCriterionId() { return yokakCriterionId; }
    public void setYokakCriterionId(String yokakCriterionId) { this.yokakCriterionId = yokakCriterionId; }
    public String getYokakCriterionCode() { return yokakCriterionCode; }
    public void setYokakCriterionCode(String yokakCriterionCode) { this.yokakCriterionCode = yokakCriterionCode; }
    public String getYokakCriterionName() { return yokakCriterionName; }
    public void setYokakCriterionName(String yokakCriterionName) { this.yokakCriterionName = yokakCriterionName; }
}