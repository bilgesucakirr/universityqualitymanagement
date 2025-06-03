package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.CreateSurveyRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.QuestionResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.SurveyResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.survey.UpdateSurveyRequest;
import com.universityqualitymanagement.universityqualitymanagement.models.Question;
import com.universityqualitymanagement.universityqualitymanagement.models.Survey;
import com.universityqualitymanagement.universityqualitymanagement.repositories.QuestionRepository;
import com.universityqualitymanagement.universityqualitymanagement.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository; // QuestionRepository'yi de ekledik

    @Autowired
    public SurveyService(SurveyRepository surveyRepository, QuestionRepository questionRepository) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
    }

    // Yeni anket oluşturma
    @Transactional
    public SurveyResponse createSurvey(CreateSurveyRequest request) {
        Survey survey = new Survey(request.getTitle(), request.getDescription());
        Survey savedSurvey = surveyRepository.save(survey); // Anketin önce kaydedilmesi gerekiyor

        List<Question> questions = request.getQuestionTexts().stream()
                .map(qText -> new Question(qText, "LIKERT_5_SCALE", savedSurvey)) // Tüm sorulara varsayılan tip atandı
                .collect(Collectors.toList());

        savedSurvey.setQuestions(questions); // İlişkiyi kur
        questionRepository.saveAll(questions); // Soruları kaydet

        return mapToSurveyResponse(savedSurvey);
    }

    // Tüm anketleri getirme
    public List<SurveyResponse> getAllSurveys() {
        return surveyRepository.findAll().stream()
                .map(this::mapToSurveyResponse)
                .collect(Collectors.toList());
    }

    // ID'ye göre anket getirme
    public SurveyResponse getSurveyById(String id) {
        return surveyRepository.findById(id)
                .map(this::mapToSurveyResponse)
                .orElseThrow(() -> new RuntimeException("Survey not found with ID: " + id));
    }

    // Anket güncelleme
    @Transactional
    public SurveyResponse updateSurvey(String id, UpdateSurveyRequest request) {
        Survey existingSurvey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found with ID: " + id));

        existingSurvey.setTitle(request.getTitle());
        existingSurvey.setDescription(request.getDescription());

        // Eski soruları silip, yeni soruları ekle
        questionRepository.deleteAll(existingSurvey.getQuestions());
        existingSurvey.getQuestions().clear();

        List<Question> newQuestions = request.getQuestionTexts().stream()
                .map(qText -> new Question(qText, "LIKERT_5_SCALE", existingSurvey))
                .collect(Collectors.toList());

        existingSurvey.setQuestions(newQuestions);
        questionRepository.saveAll(newQuestions);

        Survey updatedSurvey = surveyRepository.save(existingSurvey);
        return mapToSurveyResponse(updatedSurvey);
    }

    // Anket silme
    @Transactional
    public void deleteSurvey(String id) {
        if (!surveyRepository.existsById(id)) {
            throw new RuntimeException("Survey not found with ID: " + id);
        }
        surveyRepository.deleteById(id);
    }

    // Modelden DTO'ya dönüştürücü
    private SurveyResponse mapToSurveyResponse(Survey survey) {
        List<QuestionResponse> questionResponses = survey.getQuestions().stream()
                .map(q -> new QuestionResponse(q.getId(), q.getQuestionText(), q.getQuestionType()))
                .collect(Collectors.toList());
        return new SurveyResponse(survey.getId(), survey.getTitle(), survey.getDescription(), questionResponses);
    }
}