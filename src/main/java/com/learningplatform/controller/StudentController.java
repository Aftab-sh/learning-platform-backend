package com.learningplatform.controller;

import com.learningplatform.dto.CodeSubmitRequest;
import com.learningplatform.dto.CodeSubmitResponse;
import com.learningplatform.dto.CodingProblemDTO;
import com.learningplatform.dto.ModuleProgressDTO;
import com.learningplatform.dto.QuizQuestionDTO;
import com.learningplatform.dto.QuizSubmitRequest;
import com.learningplatform.dto.QuizSubmitResponse;
import com.learningplatform.entity.CodingProblem;
import com.learningplatform.entity.CourseEntity;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.entity.SolvedCodingProblem;
import com.learningplatform.entity.User;
import com.learningplatform.repository.CodingProblemRepository;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.SolvedCodingProblemRepository;
import com.learningplatform.service.CodingService;
import com.learningplatform.service.StudentService;
import com.learningplatform.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    
    private final StudentService studentService;
    
    
    private final CourseRepository courseRepository;
    
    
    private final CodingService codingService;
    
    private final UserService userService;
    
  
    
    
    private final CodingProblemRepository codingProblemRepository;
    
    
    private final SolvedCodingProblemRepository solvedCodingProblemRepository;

    
    @Autowired
    StudentController(
    		SolvedCodingProblemRepository solvedCodingProblemRepository,
    		CodingProblemRepository codingProblemRepository,
    		UserService userService,
    		CodingService codingService,
    		CourseRepository courseRepository,
    		StudentService studentService
    		)
    {
    	this.solvedCodingProblemRepository=solvedCodingProblemRepository;
    	this.codingProblemRepository=codingProblemRepository;
    	this.userService=userService;
    	this.codingService=codingService;
    	this.courseRepository=courseRepository;
    	this.studentService=studentService;
    	
    }
    
   
    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('STUDENT_READ_ENROLLED_COURSES')")
    //public ResponseEntity<List<CourseEntity>> getEnrolledCourses()
    public ResponseEntity<?> getEnrolledCourses()

    
    {
        // Get logged-in user's email from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Fetch enrolled courses
        List<CourseEntity> courses = studentService.getEnrolledCourses(email);

        return ResponseEntity.ok(courses);
    }
    
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasAuthority('STUDENT_WRITE_ENROLL_COURSES_BYID')")
    public ResponseEntity<?> enrollCourseByCourseId(@PathVariable Long courseId)
    {
    	Authentication auth=SecurityContextHolder.getContext().getAuthentication();
    	String email = auth.getName();
    	studentService.enrollCourseByCourseId(email,courseId);
    	return ResponseEntity.ok("Successfully enrolled in course");
    }
    
    
    @GetMapping("/course/{courseId}/modules")
    @PreAuthorize("hasAuthority('STUDENT_READ_MODULES_BY_COURSES')")

    public ResponseEntity<List<ModuleEntity>> getModulesByCourse(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        List<ModuleEntity> modules = studentService.getModulesByCourse(courseId, email);
        
        
        return ResponseEntity.ok(modules);
    }
    
    @GetMapping("/module/{moduleId}/quiz")
    @PreAuthorize("hasAuthority('STUDENT_READ_QUIZ_QUESTIONS')")

    public ResponseEntity<List<QuizQuestionDTO>> getQuizQuestions(@PathVariable Long moduleId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<QuizQuestionDTO> quiz = studentService.getQuizQuestionsByModule(moduleId, email);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/quiz/submit")
    @PreAuthorize("hasAuthority('STUDENT_WRITE_SUBMIT_QUIZE')")

    public ResponseEntity<QuizSubmitResponse> submitQuiz(@RequestBody QuizSubmitRequest request) 
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuizSubmitResponse response = studentService.submitQuiz(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @GetMapping("/progress/course/{courseId}")
    @PreAuthorize("hasAuthority('STUDENT_READ_PROGRESS')")

    public ResponseEntity<List<ModuleProgressDTO>> getProgressForCourse(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<ModuleProgressDTO> progress = studentService.getProgressForCourse(email, courseId);
        return ResponseEntity.ok(progress);
    }
    
    

    @GetMapping("/all-courses")
    @PreAuthorize("hasAuthority('STUDENT_READ_ALLCOURSES')")

    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }
    
    
    
    
    //adding judge0 
 // ── Coding Problems List ──
    
    @GetMapping("/module/{moduleId}/coding-problems")
    @PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")

    public ResponseEntity<List<CodingProblemDTO>> 
        getCodingProblems(@PathVariable Long moduleId)
    {
        return ResponseEntity.ok(
            codingService.getProblemsByModule(moduleId));
    }

    // ── Code Submit ──
    @PostMapping("/coding/submit")
    @PreAuthorize("hasAuthority('STUDENT_WRITE_SUBMIT_CODE')")

    public ResponseEntity<CodeSubmitResponse> submitCode(@RequestBody CodeSubmitRequest request)
    {
        return ResponseEntity.ok(
            codingService.submitCode(request));
    }
    
    
    

    
//    @GetMapping("/coding/solved/{moduleId}")
//    @PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")
//    public ResponseEntity<List<Long>> getSolvedProblemIds(@PathVariable Long moduleId, Authentication auth) {
//        String email = auth.getName();
//        User student = userService.findByEmail(email);
//        
//        // Module ke saare coding problem IDs
//        List<CodingProblem> problems = codingProblemRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
//        List<Long> allIds = problems.stream().map(CodingProblem::getId).collect(Collectors.toList());
//        
//        // Student ke solved problems jo is module me hain
//        List<SolvedCodingProblem> solvedList = solvedCodingProblemRepository.findByStudentId(student.getId());
//        List<Long> solvedIds = solvedList.stream()
//                .filter(s -> allIds.contains(s.getProblemId()))
//                .map(SolvedCodingProblem::getProblemId)
//                .collect(Collectors.toList());
//        
//        return ResponseEntity.ok(solvedIds);
//    }
//   
    
    @GetMapping("/coding/solved/{moduleId}")
    @PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")
    public ResponseEntity<List<Long>> getSolvedProblemIds(@PathVariable Long moduleId, Authentication auth) {
        String email = auth.getName();
        User student = userService.findByEmail(email);
        
        List<CodingProblem> problems = codingProblemRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
        List<Long> allIds = problems.stream().map(CodingProblem::getId).collect(Collectors.toList());
        
        List<SolvedCodingProblem> solvedList = solvedCodingProblemRepository.findByStudentId(student.getId());
        List<Long> solvedIds = solvedList.stream()
//                .filter(s -> allIds.contains(s.getProblemId()))
//                .map(SolvedCodingProblem::getProblemId)
        		// ✅ Fix
        		.filter(s -> allIds.contains(s.getProblem().getId()))
        		.map(s -> s.getProblem().getId())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(solvedIds);
    }
 // Get all coding problems (for practice) with optional difficulty filter
    @GetMapping("/coding-practice")
    @PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")
    public ResponseEntity<List<CodingProblem>> getCodingPracticeProblems(
            @RequestParam(required = false) String difficulty)
    {
        List<CodingProblem> problems;
        if (difficulty != null && !difficulty.isEmpty()) {
            problems = codingProblemRepository.findByDifficultyIgnoreCase(difficulty);
        } else {
            problems = codingProblemRepository.findAll();
        }
        return ResponseEntity.ok(problems);
    }

    // Get all solved problem IDs for the student (global, not module-specific)
    @GetMapping("/coding/solved/all")
    @PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")
    public ResponseEntity<List<Long>> getAllSolvedProblemIds(Authentication auth) {
        String email = auth.getName();
        User student = userService.findByEmail(email);
        List<SolvedCodingProblem> solvedList = solvedCodingProblemRepository.findByStudentId(student.getId());
        List<Long> solvedIds = solvedList.stream()
//                .map(SolvedCodingProblem::getProblemId)
        		.map(s -> s.getProblem().getId())

                .collect(Collectors.toList());
        return ResponseEntity.ok(solvedIds);
    }
    
 }