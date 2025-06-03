package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;

@Entity
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private SurveySubmission submission; // Hangi anket gönderimine ait olduğu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // Hangi soruya yanıt verildiği

    @Column(nullable = false)
    private Integer score; // Likert puanı (1-5)

    // Constructors
    public QuestionAnswer() {
    }

    public QuestionAnswer(SurveySubmission submission, Question question, Integer score) {
        this.submission = submission;
        this.question = question;
        this.score = score;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SurveySubmission getSubmission() {
        return submission;
    }

    public void setSubmission(SurveySubmission submission) {
        this.submission = submission;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}