package com.universityqualitymanagement.universityqualitymanagement.dtos.survey;

import java.util.List;

public class UpdateSurveyRequest {
    private String title;
    private String description;
    // Güncelleme için soruların da ID'leri ile birlikte gelmesi gerekebilir, şimdilik sadece metinler.
    // Daha gelişmiş bir yapı için buraya QuestionDTO eklenebilir.
    private List<String> questionTexts;

    public UpdateSurveyRequest() {
    }

    public UpdateSurveyRequest(String title, String description, List<String> questionTexts) {
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