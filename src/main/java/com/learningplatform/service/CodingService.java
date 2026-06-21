package com.learningplatform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.learningplatform.dto.CodeSubmitRequest;
import com.learningplatform.dto.CodeSubmitResponse;
import com.learningplatform.dto.CodingProblemDTO;
import com.learningplatform.entity.CodingProblem;
import com.learningplatform.entity.SolvedCodingProblem;
import com.learningplatform.entity.User;
import com.learningplatform.repository.CodingProblemRepository;
import com.learningplatform.repository.SolvedCodingProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodingService 
{

    
    private final CodingProblemRepository codingProblemRepository; 
    
    private final Judge0Service judge0Service;  
    
    private final SolvedCodingProblemRepository solvedRepo;  
    
    private final UserService userService;
    
    
    @Autowired
    CodingService(CodingProblemRepository codingProblemRepository,Judge0Service judge0Service,SolvedCodingProblemRepository solvedRepo,UserService userService)
    {
    	
    	this.codingProblemRepository=codingProblemRepository;
    	this.judge0Service=judge0Service;
    	this.solvedRepo=solvedRepo;
    	this.userService=userService;
    	
    }

    // ── Problems fetch ──
    public List<CodingProblemDTO> getProblemsByModule(Long moduleId) {
        List<CodingProblem> problems = codingProblemRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
        return problems.stream().map(p -> new CodingProblemDTO(
                p.getId(),
                p.getModule().getId(),   // ← fix
                p.getTitle(),
                p.getDescription(),
                p.getConstraintsText(),
                p.getSampleInput(),
                p.getSampleOutput(),
                p.getOrderIndex(),
                p.getDifficulty(),
                p.getMarks()
        )).collect(Collectors.toList());
    }

    // ── Submit the code ──
    @Transactional
    public CodeSubmitResponse submitCode(CodeSubmitRequest request)
    {
        try {
            // Problem fetch
            CodingProblem problem = codingProblemRepository.findById(request.getProblemId())
                    .orElseThrow(() -> new RuntimeException("Problem not found"));

            // Judge0 submit
            String token = judge0Service.submitCode(
                    request.getSourceCode(),
                    request.getLanguageId(),
                    problem.getHiddenTestInput()
            );

            // Result fetch
            JsonNode result = judge0Service.getResult(token);
            int statusId = result.get("status").get("id").asInt();

            // Output decode
            String actualOutput = judge0Service.decodeOutput(result, "stdout").trim();
            String expectedOutput = problem.getHiddenTestOutput() != null ? problem.getHiddenTestOutput().trim() : "";

            // Compilation Error
            if (statusId == 6) {
                String compileErr = judge0Service.decodeOutput(result, "compile_output");
                return new CodeSubmitResponse(false, "Compilation Error", compileErr, expectedOutput,
                        "❌ Code compile nahi hua! Syntax check karo.", 0);
            }

            // Runtime Error
            if (statusId == 11 || statusId == 12) {
                String runtimeErr = judge0Service.decodeOutput(result, "stderr");
                return new CodeSubmitResponse(false, "Runtime Error", runtimeErr, expectedOutput,
                        "❌ Runtime error aaya! Logic check karo.", 0);
            }

            // Time Limit Exceeded
            if (statusId == 5) {
                return new CodeSubmitResponse(false, "Time Limit Exceeded", "", expectedOutput,
                        "⏰ Code bahut slow hai!", 0);
            }

            // Output match
            boolean passed = actualOutput.equals(expectedOutput);

            
            
            
            
            
            if (passed)
            {
                // ✅ Save solved status in database
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();
                User student = userService.findByEmail(email);

                if (!solvedRepo.existsByStudentIdAndProblemId(student.getId(), request.getProblemId())) 
                {
                    SolvedCodingProblem solved = new SolvedCodingProblem();
//                    solved.setStudentId(student.getId());
//                    solved.setProblemId(request.getProblemId());
             

                 
                    CodingProblem problem2 = codingProblemRepository.findById(request.getProblemId())
                            .orElseThrow(() -> new RuntimeException("Problem not found"));
                    solved.setStudent(student);
                    solved.setProblem(problem2);
                    solved.setSolvedAt(LocalDateTime.now());
                    solvedRepo.save(solved);
                }

                return new CodeSubmitResponse(true, "Accepted", actualOutput, expectedOutput,
                        "🎉 Correct! All test cases passed!", problem.getMarks());
            } else {
                return new CodeSubmitResponse(false, "Wrong Answer", actualOutput, expectedOutput,
                        "❌ Wrong answer! Expected output se match nahi hua.", 0);
            }

        } catch (Exception e) {
            return new CodeSubmitResponse(false, "Error", "", "",
                    "Error: " + e.getMessage(), 0);
        }
    }
}