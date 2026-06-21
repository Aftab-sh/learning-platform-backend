package com.learningplatform.controller;

import com.learningplatform.dto.CodeSubmitRequest;
import com.learningplatform.dto.CodeSubmitResponse;
import com.learningplatform.entity.*;
import com.learningplatform.repository.*;
import com.learningplatform.service.Judge0Service;
import com.learningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('STUDENT_READ_CODINGPROBLEMS')")
public class StudentPracticeController {

    private final CodingTopicRepository codingTopicRepository;

    private final InterviewProblemRepository problemRepository;

    private final SolvedInterviewProblemRepository solvedInterviewProblemRepository;

    private final UserService userService;

    
    private final Judge0Service judge0Service;  // ✅ Judge0 injected
    
    
    @Autowired
    StudentPracticeController(
    		
    		Judge0Service judge0Service,
    		UserService userService,
    		SolvedInterviewProblemRepository solvedInterviewProblemRepository,
    		InterviewProblemRepository problemRepository,
    		CodingTopicRepository codingTopicRepository
    		
    		)
    
    {
    	this.judge0Service=judge0Service;
    	this.userService=userService;
    	this.solvedInterviewProblemRepository=solvedInterviewProblemRepository;
    	this.problemRepository=problemRepository;
    	this.codingTopicRepository=codingTopicRepository;
    	
    }

    // ─── Get all topics ───
    @GetMapping("/practice/topics")
    public ResponseEntity<List<CodingTopic>> getAllTopics() {
        return ResponseEntity.ok(codingTopicRepository.findAllByOrderByNameAsc());
    }

    // ─── Get problems filtered by topic/difficulty ───
    @GetMapping("/practice/problems")
    public ResponseEntity<List<InterviewProblem>> getPracticeProblems(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String difficulty) {

        List<InterviewProblem> problems;

        if (topicId != null && difficulty != null) {
            problems = problemRepository.findByTopicIdAndDifficulty(topicId, difficulty);
        } else if (topicId != null) {
            problems = problemRepository.findByTopicIdOrderByOrderIndexAsc(topicId);
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficulty(difficulty);
        } else {
            problems = problemRepository.findAll();
        }

        return ResponseEntity.ok(problems);
    }

    // ─── Get solved problem IDs for the current student ───
//    @GetMapping("/practice/solved")
//    public ResponseEntity<List<Long>> getSolvedPracticeProblems(Authentication auth) {
//        String email = auth.getName();
//        User student = userService.findByEmail(email);
//        List<SolvedInterviewProblem> solved = solvedInterviewProblemRepository.findByStudentId(student.getId());
//        List<Long> solvedIds = solved.stream()
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(solvedIds);
//    }
    @GetMapping("/practice/solved")
    public ResponseEntity<List<Long>> getSolvedPracticeProblems(Authentication auth) {
        String email = auth.getName();
        User student = userService.findByEmail(email);
        List<SolvedInterviewProblem> solved = solvedInterviewProblemRepository.findByStudentId(student.getId()); // ✅ method name
        List<Long> solvedIds = solved.stream()
                .map(s -> s.getProblem().getId())   // ✅ getProblem().getId()
                .collect(Collectors.toList());
        return ResponseEntity.ok(solvedIds);
    }

    // ─── Submit code for a practice problem (Judge0) ───
    @PostMapping("/practice/submit")
    public ResponseEntity<CodeSubmitResponse> submitPracticeProblem(@RequestBody CodeSubmitRequest request,
                                                                    Authentication auth) {
        String email = auth.getName();
        User student = userService.findByEmail(email);

        InterviewProblem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        try {
            // 1. Submit to Judge0
            String token = judge0Service.submitCode(request.getSourceCode(), request.getLanguageId(), problem.getHiddenTestInput());
            com.fasterxml.jackson.databind.JsonNode result = judge0Service.getResult(token);

            int statusId = result.get("status").get("id").asInt();
            String actualOutput = judge0Service.decodeOutput(result, "stdout").trim();
            String expectedOutput = problem.getHiddenTestOutput() != null ? problem.getHiddenTestOutput().trim() : "";

            boolean passed = false;
            if (statusId == 3) { // Accepted
                passed = actualOutput.equals(expectedOutput);
            }

            if (passed) {
                // Save solved status
                if (!solvedInterviewProblemRepository.existsByStudentIdAndProblemId(student.getId(), request.getProblemId())) {
                    SolvedInterviewProblem solved = new SolvedInterviewProblem();
//                    solved.setStudentId(student.getId());
//                    solved.setProblemId(request.getProblemId());
                    solved.setStudent(student);    // ← fix
                    solved.setProblem(problem); 
                    solved.setSolvedAt(LocalDateTime.now());
                    solvedInterviewProblemRepository.save(solved);
                }
                
                return ResponseEntity.ok(new CodeSubmitResponse(true, "Accepted", actualOutput, expectedOutput,
                        "🎉 Correct! All test cases passed.", problem.getMarks()));
            } else {
                String errorMsg = result.get("status").get("description").asText();
                return ResponseEntity.ok(new CodeSubmitResponse(false, errorMsg, actualOutput, expectedOutput,
                        "❌ Wrong answer or runtime error.", 0));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new CodeSubmitResponse(false, "Error", "", "", e.getMessage(), 0));
        }
    }
    
    @GetMapping("/practice/problem/{problemId}")
    public ResponseEntity<InterviewProblem> getProblemById(@PathVariable Long problemId) {
        InterviewProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        return ResponseEntity.ok(problem);
    }
    
    
}