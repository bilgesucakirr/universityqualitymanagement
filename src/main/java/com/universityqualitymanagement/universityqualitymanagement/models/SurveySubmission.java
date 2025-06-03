package com.universityqualitymanagement.universityqualitymanagement.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class SurveySubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String studentNumber; // Excel'deki OgrenciNo/Kimlik sütunundan gelecek

    @Column(nullable = false)
    private String submissionCode; // Excel'deki Kimlik sütunundan gelecek, benzersiz bir gönderim kodu

    private LocalDateTime submissionDate; // Excel'deki Cevap :de gönderildi sütunundan

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey; // Hangi ankete ait olduğu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Hangi ders için gönderildiği

    // Fakülte ve bölüm ID'lerini denormalize edebiliriz, veya Course üzerinden erişebiliriz.
    // Filterleme kolaylığı için buraya ekleyelim:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionAnswer> answers;

    // Constructors
    public SurveySubmission() {
    }

    public SurveySubmission(String studentNumber, String submissionCode, LocalDateTime submissionDate, Survey survey, Course course, Faculty faculty, Department department) {
        this.studentNumber = studentNumber;
        this.submissionCode = submissionCode;
        this.submissionDate = submissionDate;
        this.survey = survey;
        this.course = course;
        this.faculty = faculty;
        this.department = department;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getSubmissionCode() {
        return submissionCode;
    }

    public void setSubmissionCode(String submissionCode) {
        this.submissionCode = submissionCode;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }
}