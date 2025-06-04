// src/main/java/com/universityqualitymanagement/universityqualitymanagement/services/SurveyService.java (UPDATED)
package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.CreateQuestionRequest; // Corrected import: DTO
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.CreateSurveyRequest; // DTO
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.QuestionResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.UpdateQuestionRequest; // Corrected import: DTO
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.UpdateSurveyRequest; // DTO
import com.universityqualitymanagement.universityqualitymanagement.models.Question;
import com.universityqualitymanagement.universityqualitymanagement.models.YokakCriterion;
import com.universityqualitymanagement.universityqualitymanagement.repositories.QuestionRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.SurveyRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.YokakCriterionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final YokakCriterionRepository yokakCriterionRepository;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository, QuestionRepository questionRepository, YokakCriterionRepository yokakCriterionRepository) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.yokakCriterionRepository = yokakCriterionRepository;
    }

    @Transactional
    public com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey createSurvey(CreateSurveyRequest request) { // Returns DTO Survey
        com.universityqualitymanagement.universityqualitymanagement.models.Survey surveyEntity = new com.universityqualitymanagement.universityqualitymanagement.models.Survey(request.getTitle(), request.getDescription());
        com.universityqualitymanagement.universityqualitymanagement.models.Survey savedSurveyEntity = surveyRepository.save(surveyEntity);

        List<Question> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (CreateQuestionRequest qDto : request.getQuestions()) {
                YokakCriterion yokakCriterion = null;
                if (qDto.getYokakCriterionId() != null && !qDto.getYokakCriterionId().isEmpty()) {
                    yokakCriterion = yokakCriterionRepository.findById(qDto.getYokakCriterionId())
                            .orElseThrow(() -> new IllegalArgumentException("YÖKAK criterion not found with ID: " + qDto.getYokakCriterionId()));
                }
                questions.add(new Question(qDto.getQuestionText(), "LIKERT_5_SCALE", savedSurveyEntity, yokakCriterion));
            }
        }

        savedSurveyEntity.setQuestions(questions);
        questionRepository.saveAll(questions);

        return mapToSurveyResponse(savedSurveyEntity);
    }

    public List<com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey> getAllSurveys() { // Returns List<DTO Survey>
        return surveyRepository.findAll().stream()
                .map(this::mapToSurveyResponse)
                .collect(Collectors.toList());
    }

    public com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey getSurveyById(String id) { // Returns DTO Survey
        return surveyRepository.findById(id)
                .map(this::mapToSurveyResponse)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with ID: " + id));
    }

    @Transactional
    public com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey updateSurvey(String id, UpdateSurveyRequest request) { // Returns DTO Survey
        com.universityqualitymanagement.universityqualitymanagement.models.Survey existingSurveyEntity = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with ID: " + id));

        if (request.getTitle() != null) {
            existingSurveyEntity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existingSurveyEntity.setDescription(request.getDescription());
        }

        questionRepository.deleteAll(existingSurveyEntity.getQuestions());
        existingSurveyEntity.getQuestions().clear();

        List<Question> newQuestions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (UpdateQuestionRequest qDto : request.getQuestions()) {
                YokakCriterion yokakCriterion = null;
                if (qDto.getYokakCriterionId() != null && !qDto.getYokakCriterionId().isEmpty()) {
                    yokakCriterion = yokakCriterionRepository.findById(qDto.getYokakCriterionId())
                            .orElseThrow(() -> new IllegalArgumentException("YÖKAK criterion not found with ID: " + qDto.getYokakCriterionId()));
                }
                newQuestions.add(new Question(qDto.getQuestionText(), "LIKERT_5_SCALE", existingSurveyEntity, yokakCriterion));
            }
        }
        existingSurveyEntity.setQuestions(newQuestions);
        questionRepository.saveAll(newQuestions);

        com.universityqualitymanagement.universityqualitymanagement.models.Survey updatedSurveyEntity = surveyRepository.save(existingSurveyEntity);
        return mapToSurveyResponse(updatedSurveyEntity);
    }

    @Transactional
    public void deleteSurvey(String id) {
        if (!surveyRepository.existsById(id)) {
            throw new IllegalArgumentException("Survey not found with ID: " + id);
        }
        surveyRepository.deleteById(id);
    }

    // Helper method to convert Survey ENTITY to Survey DTO
    private com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey mapToSurveyResponse(com.universityqualitymanagement.universityqualitymanagement.models.Survey surveyEntity) {
        List<QuestionResponse> questionResponses = surveyEntity.getQuestions().stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getQuestionText(),
                        q.getQuestionType(),
                        q.getYokakCriterion() != null ? q.getYokakCriterion().getId() : null,
                        q.getYokakCriterion() != null ? q.getYokakCriterion().getCode() : null,
                        q.getYokakCriterion() != null ? q.getYokakCriterion().getName() : null
                ))
                .collect(Collectors.toList());
        // Call the DTO constructor here, not the Entity constructor
        return new com.universityqualitymanagement.universityqualitymanagement.dtos.survey.Survey(surveyEntity.getId(), surveyEntity.getTitle(), surveyEntity.getDescription(), questionResponses);
    }
}