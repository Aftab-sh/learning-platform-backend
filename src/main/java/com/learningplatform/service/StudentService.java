package com.learningplatform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.Exception.BadRequestException;
import com.learningplatform.Exception.ResourceNotFoundException;
import com.learningplatform.dto.ModuleProgressDTO;
import com.learningplatform.dto.QuizQuestionDTO;
import com.learningplatform.dto.QuizSubmitRequest;
import com.learningplatform.dto.QuizSubmitResponse;
import com.learningplatform.entity.*;
import com.learningplatform.repository.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;          // ✅ Added @Autowired

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;   // ✅ Added

    @Autowired
    private StudentProgressRepository progressRepository;    // ✅ Added

    
    // Helper to parse JSON options
    private List<String> parseOptions(String optionsJson) 
    {
        try 
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(optionsJson, new TypeReference<List<String>>() {});
        } 
        catch (Exception e) {
            return Arrays.asList("Option A", "Option B", "Option C", "Option D");
        }
    }

    public List<CourseEntity> getEnrolledCourses(String email) 
    {
    	log.info("Enroll request received");
    	log.info("find student by email");
        User student = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Student", "email", email));
        
        
        
        
        List<EnrollmentEntity> enrollments = enrollmentRepository.findByStudentId(student.getId());
        List<Long> courseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())   // ← getCourseId() → getCourse().getId()
                .collect(Collectors.toList());
        
        return courseRepository.findAllById(courseIds);
    }

    public void enrollCourseByCourseId(String email, Long courseId) 
    {

    	 log.info("enrollCourseByCourseId ");
    	 log.info("Find Studnet");
        User student = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Student", "email", email));

        log.info("Finding course by course ID ");
    	CourseEntity course = courseRepository.findById(courseId)
    			.orElseThrow(() ->
    		    new ResourceNotFoundException("Course", "id", courseId));
    	

    	log.info("Saving enrollment");
    	
    	EnrollmentEntity enrollment = new EnrollmentEntity();
    	enrollment.setStudent(student);    // ← setStudentId() → setStudent()
    	enrollment.setCourse(course);      // ← setCourseId() → setCourse()
    	enrollment.setEnrolledAt(LocalDateTime.now());
    	enrollmentRepository.save(enrollment);
    }

   
    public List<ModuleEntity> getModulesByCourse(Long courseId, String email) 
    {
    	log.info("Fetching modules");
    	
    	log.info("find student by email");
        User student = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Student", "email", email));
//                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
        if (!isEnrolled)
        {
        	log.info("User already enrolled");
        	throw new BadRequestException("You are not enrolled in this course"); 
        	}

    	log.info("find Modules by course id");
        List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // 🔍 Debug start
        System.out.println("\n========== MODULE LOCK DEBUG ==========");
        System.out.println("Course ID: " + courseId + ", Student: " + student.getEmail());

        for (int i = 0; i < modules.size(); i++)
        {
            ModuleEntity current = modules.get(i);
            if (i == 0)
            {
                current.setLocked(false);
                System.out.println("Module " + (i+1) + " (" + current.getTitle() + ") → First module → locked = false");
            } 
            else 
            {
                ModuleEntity prev = modules.get(i - 1);
                Optional<StudentProgress> prevProgressOpt = progressRepository.findByStudentIdAndModuleId(student.getId(), prev.getId());
                
                boolean prevCompleted = false;
                if (prevProgressOpt.isPresent())
                {
                    StudentProgress prevProgress = prevProgressOpt.get();
                    prevCompleted = Boolean.TRUE.equals(prevProgress.getModuleCompleted());
                    System.out.println("Module " + i + " (" + prev.getTitle() + ") → Progress exists, moduleCompleted = " + prevCompleted + ", quizScore = " + prevProgress.getQuizScore());
                } 
                else
                {
                    System.out.println("Module " + i + " (" + prev.getTitle() + ") → NO progress record");
                }
                
                current.setLocked(!prevCompleted);
                System.out.println("Module " + (i+1) + " (" + current.getTitle() + ") → locked = " + current.isLocked() + " (because prevCompleted=" + prevCompleted + ")");
            }
        }
        log.info("Modules fatch successfully");     
        return modules;
    }

    
    public List<QuizQuestionDTO> getQuizQuestionsByModule(Long moduleId, String email)
    {
    	log.info("Get QuizQuestion by Module");
        // Check enrollment: find module's course
    	
    	log.info("Find Module by moduleId");
        ModuleEntity module = moduleRepository.findById(moduleId)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Module", "id", moduleId)
        	    );
//                .orElseThrow(() -> new RuntimeException("Module not found"));
        
        log.info("Find Student by email");
        User student = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Student", "email", email));        
//        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), module.getCourseId());
     // ✅ FIX 3 — getQuizQuestionsByModule() mein
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), module.getCourse().getId());  // ← getCourseId() → getCourse().getId()
        
        
        
        if (!isEnrolled) 
        {
        	log.info("Student is not enrolled in this course ");
            throw new BadRequestException("You are not enrolled in this course");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByModuleId(moduleId);
        return questions.stream().map(q ->
        {
            List<String> options = parseOptions(q.getOptions());
            return new QuizQuestionDTO(q.getId(), q.getQuestionText(), options, q.getMarks());
        }
        ).collect(Collectors.toList());
    }

    
    public QuizSubmitResponse submitQuiz(String email, QuizSubmitRequest request) 
    {
    	log.info("Quiz submission started");
        Long moduleId = request.getModuleId();
        Map<Long, Integer> answers = request.getAnswers();

        log.info("Find Student ");
        User student = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Student", "email", email));
        
            log.info("fatching Module by module ID");
        ModuleEntity module = moduleRepository.findById(moduleId)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("Module", "id", moduleId));
//                .orElseThrow(() -> new RuntimeException("Module not found"));
        
//        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), module.getCourseId());
     // ✅ FIX 4 — submitQuiz() mein
        log.info("Check Student is enrolled or Not");
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), module.getCourse().getId());  // ← getCourseId() → getCourse().getId()
        if (!isEnrolled)
        {
        	log.info("Student is not Enrolled");
            throw new BadRequestException("You are not enrolled in this course");
        }

        log.info("find  question by module ID");
        List<QuizQuestion> questions = quizQuestionRepository.findByModuleId(moduleId);
        if (questions.isEmpty())
        {
        	log.info("Question is not found ");
        	throw new ResourceNotFoundException(
        		    "Quiz Questions",
        		    "moduleId",
        		    moduleId
        		);
//            throw new RuntimeException("No quiz questions found for this module");
        }

        int totalMarks = 0;
        int obtainedMarks = 0;
        for (QuizQuestion q : questions)
        {
            totalMarks += q.getMarks();
            Integer selected = answers.get(q.getId());
            if (selected != null && selected.equals(q.getCorrectOption()))
            {
                obtainedMarks += q.getMarks();
            }
        }
        double percentage = (double) obtainedMarks / totalMarks * 100;
        boolean passed = percentage >= module.getPassingPercentage();

        // Update progress
        log.info("update Progress in quiz question");
        StudentProgress progress = progressRepository.findByStudentIdAndModuleId(student.getId(), moduleId)
                .orElse(new StudentProgress());
        progress.setStudentId(student.getId());
        progress.setModuleId(moduleId);
        progress.setQuizScore((int) Math.round(percentage));
        if (passed && !Boolean.TRUE.equals(progress.getModuleCompleted())) {
            progress.setModuleCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        } else if (!passed) {
            progress.setModuleCompleted(false);
            progress.setCompletedAt(null);
        }
        progressRepository.save(progress);

        String message = passed ? "Congratulations! You passed the quiz." : "You did not pass. Please try again.";
        return new QuizSubmitResponse(percentage, passed, message);
    }
    
    

    public List<ModuleProgressDTO> getProgressForCourse(String email, Long courseId)
    {
        // 1. Get student
    	log.info("Student get Progress");
    	
    	log.info("find student by email");
        User student = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("student", "email", email));
//                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // 2. Check enrollment
        log.info("Check Enrollment");
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
        if (!isEnrolled) 
        {
        	log.info("Student is not enrolled in this course");
            throw new BadRequestException("You are not enrolled in this course");
        }
        
        // 3. Fetch all modules of the course (ordered)
        
        List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        
        // 4. Build progress DTO for each module
        List<ModuleProgressDTO> progressList = new ArrayList<>();
        for (ModuleEntity module : modules) {
            Optional<StudentProgress> optProgress = progressRepository.findByStudentIdAndModuleId(student.getId(), module.getId());
            
            ModuleProgressDTO dto = new ModuleProgressDTO();
            dto.setModuleId(module.getId());
            dto.setModuleTitle(module.getTitle());
            dto.setOrderIndex(module.getOrderIndex());
            
            if (optProgress.isPresent()) {
                StudentProgress p = optProgress.get();
                dto.setCompleted(p.getModuleCompleted());
                dto.setQuizScore(p.getQuizScore());
                dto.setCompletedAt(p.getCompletedAt());
            } else {
                dto.setCompleted(false);
                dto.setQuizScore(0);
                dto.setCompletedAt(null);
            }
            progressList.add(dto);
        }
        return progressList;
    }

	
}