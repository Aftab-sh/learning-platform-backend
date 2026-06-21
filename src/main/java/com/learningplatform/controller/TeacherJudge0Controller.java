package com.learningplatform.controller;

import com.learningplatform.entity.CodingProblem;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.repository.CodingProblemRepository;
import com.learningplatform.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherJudge0Controller {

    
    private final CodingProblemRepository codingProblemRepository;
   
    
    private final ModuleRepository moduleRepository;
    
    
    @Autowired
    TeacherJudge0Controller
    (
    		ModuleRepository moduleRepository,
    		CodingProblemRepository codingProblemRepository
    		)
    {
    	this.moduleRepository=moduleRepository;
    	this.codingProblemRepository=codingProblemRepository;
    	
    }
    

    // Get all coding problems for a module
    @GetMapping("/coding-problems/{moduleId}")
    public ResponseEntity<List<CodingProblem>> getCodingProblemsByModule(@PathVariable Long moduleId) {
        List<CodingProblem> problems = codingProblemRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
        return ResponseEntity.ok(problems);
    }

    // Create a new coding problem
    @PostMapping("/coding-problems/{moduleId}")
    public ResponseEntity<CodingProblem> createCodingProblem(@PathVariable Long moduleId,
                                                             @RequestBody CodingProblem problem) {
//        problem.setModuleId(moduleId);
    	
    	ModuleEntity module = moduleRepository.findById(moduleId)
    	        .orElseThrow(() -> new RuntimeException("Module not found"));
    	problem.setModule(module);
        // Ensure orderIndex is set, otherwise set a default
        if (problem.getOrderIndex() == 0) {
            // Get max orderIndex for this module and add 1
            List<CodingProblem> existing = codingProblemRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
            int maxOrder = existing.stream().mapToInt(CodingProblem::getOrderIndex).max().orElse(0);
            problem.setOrderIndex(maxOrder + 1);
        }
        CodingProblem saved = codingProblemRepository.save(problem);
        return ResponseEntity.ok(saved);
    }

    // Update an existing coding problem
    @PutMapping("/coding-problems/{id}")
    public ResponseEntity<CodingProblem> updateCodingProblem(@PathVariable Long id,
                                                             @RequestBody CodingProblem problem) {
        // Ensure the problem exists
        CodingProblem existing = codingProblemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coding problem not found with id: " + id));
        existing.setTitle(problem.getTitle());
        existing.setDescription(problem.getDescription());
        existing.setConstraintsText(problem.getConstraintsText());
        existing.setSampleInput(problem.getSampleInput());
        existing.setSampleOutput(problem.getSampleOutput());
        existing.setHiddenTestInput(problem.getHiddenTestInput());
        existing.setHiddenTestOutput(problem.getHiddenTestOutput());
        existing.setDifficulty(problem.getDifficulty());
        existing.setMarks(problem.getMarks());
        existing.setOrderIndex(problem.getOrderIndex());
        // externalLink can be set if needed
        CodingProblem updated = codingProblemRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    // Delete a coding problem
    @DeleteMapping("/coding-problems/{id}")
    public ResponseEntity<String> deleteCodingProblem(@PathVariable Long id) {
        if (!codingProblemRepository.existsById(id)) {
            throw new RuntimeException("Coding problem not found with id: " + id);
        }
        codingProblemRepository.deleteById(id);
        return ResponseEntity.ok("Coding problem deleted successfully");
    }
    
    @GetMapping("/course/{courseId}/modules")
    public ResponseEntity<List<ModuleEntity>> getModulesForTeacher(@PathVariable Long courseId) {
        List<ModuleEntity> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return ResponseEntity.ok(modules);
    }
}