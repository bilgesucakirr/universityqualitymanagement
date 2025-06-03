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

@Service
public class ExcelUploadService {

    private final SurveyRepository surveyRepository;
    private final SurveySubmissionRepository surveySubmissionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

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
    public String uploadSurveyResults(MultipartFile file, String surveyId) throws IOException {
        // Find the survey by ID
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with ID: " + surveyId));

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

        // Read the header row and find column indices
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Excel file is empty or missing header row.");
        }

        Map<String, Integer> colIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            // Trim column names to remove any whitespace that might exist from Excel export
            colIndexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }

        // Check for existence of required columns
        // Adjusted "Cevap :de gönderildi" column name for trimming
        List<String> requiredCols = List.of("Cevap\t:de gönderildi", "Kurum", "Bölüm", "Ders", "Kimlik");
        for (String col : requiredCols) {
            if (!colIndexMap.containsKey(col)) {
                throw new IllegalArgumentException("Missing required column in Excel: " + col);
            }
        }

        // Find the indices of each Likert question column (Q01 to Q30)
        // Map Excel column index to the corresponding Question object from the Survey
        Map<Integer, Question> questionMap = new HashMap<>();
        for (int i = 1; i <= 30; i++) { // Assuming Q01 to Q30 are Likert questions
            String baseQuestionHeader = String.format("Q%02d", i);
            String fullQuestionHeader = null;

            // Try different possible header formats from the Excel example
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
                if (colIdx != null && (i - 1) < survey.getQuestions().size()) {
                    // Q01 maps to survey.getQuestions().get(0), Q02 to get(1), etc.
                    questionMap.put(colIdx, survey.getQuestions().get(i - 1));
                }
            } else {
                // If Likert question column not found, check if it's beyond Q30 (Q31, Q32 etc.)
                // These are non-Likert according to the sample, so we can skip.
                if (i <= 30) { // If it's one of the expected Likert questions and not found, throw error
                    throw new IllegalArgumentException("Missing Likert question column in Excel: " + baseQuestionHeader);
                }
            }
        }

        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
            final Row dataRow = sheet.getRow(i);
            if (dataRow == null) continue; // Skip empty rows

            // Extract the submission code from "Kimlik" column
            final String submissionCode = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Kimlik")));
            // Check if this submission already exists to prevent duplicates
            if (surveySubmissionRepository.existsBySubmissionCode(submissionCode)) {
                System.out.println("Submission with code " + submissionCode + " already exists. Skipping.");
                continue;
            }

            // Extract student number (using "Kimlik" column for student number as per example)
            String tempStudentNumber = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Kimlik"))); // Assuming "Kimlik" is student identifier
            if (tempStudentNumber.isEmpty() || tempStudentNumber.isBlank()) {
                // If student number is empty, anonymize it using submissionCode
                tempStudentNumber = "Anonim_" + submissionCode;
            }
            final String studentNumber = tempStudentNumber; // Effective final for closure

            // Parse submission date (this part is crucial and error-prone due to irregular format)
            LocalDateTime submissionDate = null; // Removed final, initialized to null
            final String submissionDateStr = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Cevap\t:de gönderildi")));
            try {
                // Regex to extract month/day, year, and time parts
                // Example: "9/am/2024 09:01:18"
                // Group 1: month (e.g., "9")
                // Group 2: empty or "am" (ignored)
                // Group 3: year (e.g., "2024")
                // Group 4-6: hour, minute, second
                Pattern pattern = Pattern.compile("(\\d+)/\\w*(\\d*)/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})");
                Matcher matcher = pattern.matcher(submissionDateStr);

                if (matcher.find()) {
                    String monthOrDayPart = matcher.group(1); // "9"
                    // If your Excel date includes a day (e.g., "9/15/2024"), matcher.group(2) would be "15"
                    // As per the example, "9/am/2024", so we assume day is not explicitly given in the first two parts,
                    // and we will default to '01' for the day to create a valid date.
                    String yearPart = matcher.group(3); // "2024"
                    String timePart = String.format("%s:%s:%s", matcher.group(4), matcher.group(5), matcher.group(6));

                    // Force "month/day/year" format: e.g., "9/am/2024" -> "09/01/2024"
                    // If the Excel format varies (e.g., "M/d/yyyy"), this part might need adjustment.
                    String datePartCleaned = String.format("%s/01/%s", monthOrDayPart, yearPart);

                    // Parse the date and time string
                    submissionDate = LocalDateTime.parse(datePartCleaned + " " + timePart, DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm:ss"));
                } else {
                    System.err.println("Failed to match date pattern for: " + submissionDateStr + ". Defaulting to current time.");
                    submissionDate = LocalDateTime.now(); // Default to current time if parsing fails
                }
            } catch (Exception e) {
                System.err.println("Error parsing date '" + submissionDateStr + "': " + e.getMessage() + ". Defaulting to current time.");
                submissionDate = LocalDateTime.now(); // Default to current time on any parsing error
            }

            final String institutionName = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Kurum")));
            final String departmentName = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Bölüm")));
            final String courseExcelString = formatter.formatCellValue(dataRow.getCell(colIndexMap.get("Ders")));

            // Parse Course Excel string: e.g., "INDE2001.1 Operations Research I (4) (FALL23)"
            String tempCourseCode = "";
            String tempCourseName = "";
            Integer tempCredits = null;
            String tempSemester = "";

            // Regex: CourseCode (e.g., INDE2001.1), CourseName (e.g., Operations Research I), Credits (e.g., 4), Semester (e.g., FALL23)
            Pattern coursePattern = Pattern.compile("([A-Z]{4}\\d{4}\\.\\d+)\\s+(.*)\\s+\\((\\d+)\\)\\s+\\(([^)]+)\\)");
            Matcher courseMatcher = coursePattern.matcher(courseExcelString);
            if (courseMatcher.find()) {
                tempCourseCode = courseMatcher.group(1);
                tempCourseName = courseMatcher.group(2).trim();
                tempCredits = Integer.parseInt(courseMatcher.group(3));
                tempSemester = courseMatcher.group(4);
            } else {
                System.err.println("Could not parse course string: " + courseExcelString + ". Skipping this row.");
                continue; // Skip row if course parsing fails
            }

            // Make effectively final copies for Course object creation
            final String courseCode = tempCourseCode;
            final String courseName = tempCourseName;
            final Integer credits = tempCredits;
            final String semester = tempSemester;

            // Find or create Faculty
            final Faculty faculty = facultyRepository.findByName(institutionName)
                    .orElseGet(() -> facultyRepository.save(new Faculty(institutionName)));

            // Find or create Department (associated with Faculty)
            final Department department = departmentRepository.findByNameAndFaculty(departmentName, faculty)
                    .orElseGet(() -> departmentRepository.save(new Department(departmentName, faculty)));

            // Instructor information is not directly in the Excel example, so leave as null
            final Instructor instructor = null;

            // Find or create Course (associated with Department and Instructor)
            final Course course = courseRepository.findByCourseCodeAndSemesterAndDepartment(courseCode, semester, department)
                    .orElseGet(() -> courseRepository.save(new Course(courseCode, courseName, credits, semester, department, instructor)));

            // Create and save SurveySubmission
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
                        final Integer score = (int) scoreCell.getNumericCellValue(); // Likert score should be numeric
                        answers.add(new QuestionAnswer(submission, question, score));
                    } catch (IllegalStateException | NumberFormatException e) {
                        System.err.println("Invalid score format for question at column " + colIdx + " in row " + i + ": " + formatter.formatCellValue(scoreCell) + ". Skipping this answer.");
                    }
                }
            }
            questionAnswerRepository.saveAll(answers);
        }

        workbook.close();
        return "Survey results uploaded successfully!";
    }
}