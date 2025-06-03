package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String questionText;

    // Örneğin "LIKERT_5_SCALE" gibi bir değer tutabiliriz
    private String questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    public Question() {
    }

    public Question(String questionText, String questionType, Survey survey) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.survey = survey;
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

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}