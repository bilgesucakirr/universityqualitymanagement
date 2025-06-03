package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.services.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class DataUploadController {

    private final ExcelUploadService excelUploadService;

    @Autowired
    public DataUploadController(ExcelUploadService excelUploadService) {
        this.excelUploadService = excelUploadService;
    }

    @PostMapping("/survey-results")
    public ResponseEntity<String> uploadSurveyResults(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("surveyId") String surveyId) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }
        try {
            String message = excelUploadService.uploadSurveyResults(file, surveyId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return new ResponseEntity<>("Failed to upload survey results: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}