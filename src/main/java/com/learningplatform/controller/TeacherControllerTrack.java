package com.learningplatform.controller;

import com.learningplatform.entity.*;
import com.learningplatform.repository.*;
import com.learningplatform.service.CourseService;
import com.learningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherControllerTrack {

    
    private final UserRepository userRepository;

    
    private final UserService userService;

    
    private final CourseService courseService;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    
    private final ModuleRepository moduleRepository;

    
    private final StudentProgressRepository progressRepository;

    
    private final SolvedInterviewProblemRepository solvedInterviewProblemRepository;
    
    @Autowired
    TeacherControllerTrack
    (
    		SolvedInterviewProblemRepository solvedInterviewProblemRepository,
    		ModuleRepository moduleRepository,
    		EnrollmentRepository enrollmentRepository,
    		StudentProgressRepository progressRepository,
    		CourseRepository courseRepository,
    		CourseService courseService,
    		UserService userService,
    		UserRepository userRepository
    		
    		)
    {
    	this.solvedInterviewProblemRepository=solvedInterviewProblemRepository;
    	this.moduleRepository=moduleRepository;
    	this.enrollmentRepository=enrollmentRepository;
    	this.progressRepository=progressRepository;
    	this.courseRepository=courseRepository;
    	this.courseService=courseService;
    	this.userService=userService;
    	this.userRepository=userRepository;
    	
    	
    }
    

    // ─── Get all students (basic info) ───
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        return ResponseEntity.ok(students);
    }

    // ─── Get enrollment stats for teacher's courses ───
    @GetMapping("/enrollment-stats")
    public ResponseEntity<List<Map<String, Object>>> getEnrollmentStats(Authentication auth) {
        String email = auth.getName();
        User teacher = userService.findByEmail(email);
        List<CourseEntity> courses = courseService.getCourseByTeacher(teacher.getId());
        List<Map<String, Object>> stats = new ArrayList<>();
        for (CourseEntity c : courses) {
            long enrolled = enrollmentRepository.countByCourseId(c.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("courseId", c.getId());
            map.put("courseTitle", c.getTitle());
            map.put("enrolledStudents", enrolled);
            stats.add(map);
        }
        return ResponseEntity.ok(stats);
    }

    // ─── Get detailed progress for a specific student (by ID) ───
    @GetMapping("/student-progress/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentProgress(@PathVariable Long studentId) {
        User student = userService.findById(studentId);
        if (student == null || student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Student not found");
        }

        // Get enrolled courses
        List<EnrollmentEntity> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<Map<String, Object>> courseProgress = new ArrayList<>();

        for (EnrollmentEntity e : enrollments) 
        {
            CourseEntity course = courseRepository.findById(e.getCourse().getId()).orElse(null);

            if (course == null) continue;

            List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
            // Get progress for this student in this course (all modules)
            List<StudentProgress> allProgress = progressRepository.findByStudentId(studentId);
            // Better: count only modules belonging to this course
            Set<Long> moduleIds = modules.stream().map(ModuleEntity::getId).collect(Collectors.toSet());
            long completedInCourse = allProgress.stream()
                    .filter(p -> p.getModuleCompleted() && moduleIds.contains(p.getModuleId()))
                    .count();

            double percentage = modules.isEmpty() ? 0 : (completedInCourse * 100.0 / modules.size());

            Map<String, Object> cp = new HashMap<>();
            cp.put("courseId", course.getId());
            cp.put("courseTitle", course.getTitle());
            cp.put("totalModules", modules.size());
            cp.put("completedModules", completedInCourse);
            cp.put("percentage", Math.round(percentage));
            courseProgress.add(cp);
        }

        // Solved interview problems count
        long solvedInterview = solvedInterviewProblemRepository.countByStudentId(studentId);

        Map<String, Object> result = new HashMap<>();
        result.put("student", student);
        result.put("courseProgress", courseProgress);
        result.put("solvedInterviewProblems", solvedInterview);
        return ResponseEntity.ok(result);
    }
}