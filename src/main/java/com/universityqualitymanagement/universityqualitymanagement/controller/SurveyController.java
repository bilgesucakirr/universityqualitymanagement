// src/main/java/com/universityqualitymanagement/universityqualitymanagement/controller/SurveyController.java
package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.CreateSurveyRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey; // Corrected: Using the DTO named 'Survey'
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
    public ResponseEntity<Survey> createSurvey(@RequestBody CreateSurveyRequest request) { // Corrected return type
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        Survey response = surveyService.createSurvey(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() { // Corrected return type
        // SECURITY NOTE: Unprotected endpoint.
        List<Survey> surveys = surveyService.getAllSurveys();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurveyById(@PathVariable String id) { // Corrected return type
        // SECURITY NOTE: Unprotected endpoint.
        Survey survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Survey> updateSurvey(@PathVariable String id, @RequestBody UpdateSurveyRequest request) { // Corrected return type
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        Survey updatedSurvey = surveyService.updateSurvey(id, request);
        return ResponseEntity.ok(updatedSurvey);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable String id) {
        // SECURITY NOTE: Unprotected endpoint. Admin/Staff access required in a real app.
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }
}