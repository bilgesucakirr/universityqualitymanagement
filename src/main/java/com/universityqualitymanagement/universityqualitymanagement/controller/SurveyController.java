package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.CreateSurveyRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.SurveyResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.UpdateSurveyRequest;
import com.universityqualitymanagement.universityqualitymanagement.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping
    public ResponseEntity<SurveyResponse> createSurvey(@RequestBody CreateSurveyRequest request) {
        SurveyResponse response = surveyService.createSurvey(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SurveyResponse>> getAllSurveys() {
        List<SurveyResponse> surveys = surveyService.getAllSurveys();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponse> getSurveyById(@PathVariable String id) {
        SurveyResponse survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponse> updateSurvey(@PathVariable String id, @RequestBody UpdateSurveyRequest request) {
        SurveyResponse updatedSurvey = surveyService.updateSurvey(id, request);
        return ResponseEntity.ok(updatedSurvey);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable String id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    // --- Önemli Not: Güvenlik için (@PreAuthorize gibi) buraya daha sonra yetkilendirme eklenmelidir. ---
    // @ExceptionHandler ile daha iyi hata yönetimi de yapılabilir.
}