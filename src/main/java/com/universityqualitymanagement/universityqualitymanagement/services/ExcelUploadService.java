package com.universityqualitymanagement.universityqualitymanagement.services;

import com.universityqualitymanagement.universityqualitymanagement.models.*;
import com.universityqualitymanagement.universityqualitymanagement.repositories.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

@Service
public class ExcelUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUploadService.class); // Initialize logger

    private final SurveyRepository surveyRepository;
    private final SurveySubmissionRepository surveySubmissionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final FacultyRepository facultyRepository; // Still needed for lookup
    private final DepartmentRepository departmentRepository; // Still needed for lookup
    private final CourseRepository courseRepository; // Still needed for lookup
    private final InstructorRepository instructorRepository; // Still needed if instructors are linked to courses

    @Autowired
    public ExcelUploadService(SurveyRepository surveyRepository,
                              SurveySubmissionRepository surveySubmissionRepository,
                              QuestionAnswerRepository questionAnswerRepository,
                              FacultyRepository facultyRepository,
                              DepartmentRepository departmentRepository,
                              CourseRepository courseRepository,
                              InstructorRepository instructorRepository) {
        this.surveyRepository = surveyRepository;
        this.surveySubmissionRepository = surveySubmissionRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public String uploadSurveyResults(MultipartFile file, String surveyId, String facultyId, String departmentId, String courseId) throws IOException {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with ID: " + surveyId));

        // Find the pre-selected university entities
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Selected Faculty not found with ID: " + facultyId));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Selected Department not found with ID: " + departmentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Selected Course not found with ID: " + courseId));

        // Optional: Add validation that the selected course belongs to the selected department/faculty
        if (!course.getDepartment().getId().equals(department.getId())) {
            throw new IllegalArgumentException("Selected Course does not belong to the selected Department.");
        }
        if (!department.getFaculty().getId().equals(faculty.getId())) {
            throw new IllegalArgumentException("Selected Department does not belong to the selected Faculty.");
        }


        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Excel file is empty or missing header row.");
        }

        Map<String, Integer> colIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            colIndexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }

        List<String> requiredCols = List.of("Cevap\t:de gönderildi", "Kimlik"); // "Kurum", "Bölüm", "Ders" are now provided as IDs
        for (String col : requiredCols) {
            if (!colIndexMap.containsKey(col)) {
                throw new IllegalArgumentException("Missing required column in Excel: " + col);
            }
        }

        // Map Excel column index to the corresponding Question object from the Survey
        Map<Integer, Question> questionMap = new HashMap<>();
        for (int i = 1; i <= 30; i++) { // Assuming Q01 to Q30 are Likert questions
            String baseQuestionHeader = String.format("Q%02d", i);
            String fullQuestionHeader = null;

            String[] potentialHeaders = {
                    baseQuestionHeader + "->1-Strongly disagree.... 5-Strongly agree",
                    baseQuestionHeader + "->1. Çok Az / Very Low ... 5. Çok Yüksek / Very High",
                    baseQuestionHeader + "->1. Strongly disagree/Kesinlikle katılmıyorum .. 5. Kesinlikle katılıyorum / Strongly agree",
                    baseQuestionHeader + "_tekrar der->1-Strongly disagree.... 5-Strongly agree"
            };

            for(String header : potentialHeaders) {
                if (colIndexMap.containsKey(header)) {
                    fullQuestionHeader = header;
                    break;
                }
            }

            if (fullQuestionHeader != null) {
                Integer colIdx = colIndexMap.get(fullQuestionHeader);
                // Important: Link question to actual question from survey entity, not by rigid index 'i-1'.
                // This assumes questions in Survey are sorted by their default excel parsing order (Q01, Q02...).
                // A more robust method would be to parse the full question text from Excel header and match it to survey.questions.
                // Or if Question entity had a specific 'excelColumnIdentifier' field.
                if (colIdx != null && (i - 1) < survey.getQuestions().size()) {
                    questionMap.put(colIdx, survey.getQuestions().get(i - 1));
                }
            } else {
                // If a Likert question column isn't found for Q01-Q30, it indicates a problem with the Excel format.
                if (i <= 30) {
                    logger.warn("Likert question column {} not found in Excel. Skipping scores for this question.", baseQuestionHeader);
                    // If you want to throw an error for missing expected columns:
                    // throw new IllegalArgumentException("Missing Likert question column in Excel: " + baseQuestionHeader);
                }
            }
        }

        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            final Row dataRow = sheet.getRow(i);
            if (dataRow == null) continue;

            final String submissionCode = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Kimlik")));
            if (surveySubmissionRepository.existsBySubmissionCode(submissionCode)) {
                logger.info("Submission with code {} already exists. Skipping this row.", submissionCode);
                continue;
            }

            String studentNumber = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Kimlik")));
            if (studentNumber.isEmpty() || studentNumber.isBlank()) {
                studentNumber = "Anonim_" + submissionCode; // Anonymize if empty
            }

            LocalDateTime submissionDate = null;
            final String submissionDateStr = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Cevap\t:de gönderildi")));
            try {
                // Regex: M/day/yyyy HH:mm:ss -> (M)/anything/(yyyy) (HH):(mm):(ss)
                Pattern pattern = Pattern.compile("(\\d+)/\\w*(\\d*)/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})");
                Matcher matcher = pattern.matcher(submissionDateStr);

                if (matcher.find()) {
                    String monthPart = matcher.group(1);
                    String yearPart = matcher.group(3);
                    String timePart = String.format("%s:%s:%s", matcher.group(4), matcher.group(5), matcher.group(6));
                    String datePartCleaned = String.format("%s/01/%s", monthPart, yearPart); // Default to 01 for day

                    submissionDate = LocalDateTime.parse(datePartCleaned + " " + timePart, DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm:ss"));
                } else {
                    logger.warn("Failed to match date pattern for: '{}'. Defaulting to current time.", submissionDateStr);
                    submissionDate = LocalDateTime.now();
                }
            } catch (Exception e) {
                logger.error("Error parsing date '{}': {}. Defaulting to current time.", submissionDateStr, e.getMessage());
                submissionDate = LocalDateTime.now();
            }

            // Create and save SurveySubmission using the pre-selected entities
            final SurveySubmission submission = new SurveySubmission(studentNumber, submissionCode, submissionDate, survey, course, faculty, department);
            surveySubmissionRepository.save(submission);

            // Save Likert question answers for the submission
            List<QuestionAnswer> answers = new ArrayList<>();
            for (Map.Entry<Integer, Question> entry : questionMap.entrySet()) {
                final Integer colIdx = entry.getKey();
                final Question question = entry.getValue();

                final Cell scoreCell = dataRow.getCell(colIdx);
                if (scoreCell != null) {
                    try {
                        final Integer score = (int) scoreCell.getNumericCellValue();
                        // Basic validation for Likert scale scores (1-5)
                        if (score >= 1 && score <= 5) {
                            answers.add(new QuestionAnswer(submission, question, score));
                        } else {
                            logger.warn("Invalid Likert score ({}) for question '{}' in row {}. Skipping this answer.", score, question.getQuestionText(), i);
                        }
                    } catch (IllegalStateException | NumberFormatException e) {
                        logger.warn("Invalid score format for question at column {} in row {}: '{}'. Skipping this answer.", colIdx, i, formatter.formatCellValue(scoreCell));
                    }
                }
            }
            questionAnswerRepository.saveAll(answers);
        }

        workbook.close();
        return "Survey results uploaded successfully!";
    }
}