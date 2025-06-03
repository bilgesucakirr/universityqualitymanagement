package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

public class QuestionResponse {
    private String id;
    private String questionText;
    private String questionType;

    public QuestionResponse() {
    }

    public QuestionResponse(String id, String questionText, String questionType) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}