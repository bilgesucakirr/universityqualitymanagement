package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    private String questionType; // e.g., "LIKERT_5_SCALE"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY) // Link to YokakCriterion (usually SUB_CRITERION level)
    @JoinColumn(name = "yokak_criterion_id") // Foreign key column
    private YokakCriterion yokakCriterion; // The YÖKAK criterion this question belongs to

    public Question() {}

    // Updated constructor to include YokakCriterion
    public Question(String questionText, String questionType, Survey survey, YokakCriterion yokakCriterion) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.survey = survey;
        this.yokakCriterion = yokakCriterion;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public Survey getSurvey() { return survey; }
    public void setSurvey(Survey survey) { this.survey = survey; }
    public YokakCriterion getYokakCriterion() { return yokakCriterion; }
    public void setYokakCriterion(YokakCriterion yokakCriterion) { this.yokakCriterion = yokakCriterion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}