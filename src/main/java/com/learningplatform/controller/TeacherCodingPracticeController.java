package com.learningplatform.controller;

import com.learningplatform.entity.CodingTopic;
import com.learningplatform.entity.InterviewProblem;
import com.learningplatform.repository.CodingTopicRepository;
import com.learningplatform.repository.InterviewProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherCodingPracticeController {

    
    private final CodingTopicRepository topicRepository;

    
    private final InterviewProblemRepository problemRepository;
    
    @Autowired
    TeacherCodingPracticeController(CodingTopicRepository topicRepository,
    		InterviewProblemRepository problemRepository)
    {
    	this.problemRepository=problemRepository;
    	this.topicRepository=topicRepository;
    	
    }
    
    

    // ─────────────────────────────────────────
    // TOPIC CRUD
    // ─────────────────────────────────────────
    @PostMapping("/topics")
    public ResponseEntity<CodingTopic> createTopic(@RequestBody CodingTopic topic) {
        return ResponseEntity.ok(topicRepository.save(topic));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<CodingTopic>> getAllTopics() {
        return ResponseEntity.ok(topicRepository.findAllByOrderByNameAsc());
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<CodingTopic> updateTopic(@PathVariable Long id, @RequestBody CodingTopic topic) {
        topic.setId(id);
        return ResponseEntity.ok(topicRepository.save(topic));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<String> deleteTopic(@PathVariable Long id) {
        topicRepository.deleteById(id);
        return ResponseEntity.ok("Topic deleted");
    }

    // ─────────────────────────────────────────
    // INTERVIEW PROBLEM CRUD
    // ─────────────────────────────────────────
    @PostMapping("/interview-problems")
    public ResponseEntity<InterviewProblem> createProblem(@RequestBody InterviewProblem problem) {
        return ResponseEntity.ok(problemRepository.save(problem));
    }

    @GetMapping("/interview-problems/topic/{topicId}")
    public ResponseEntity<List<InterviewProblem>> getProblemsByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(problemRepository.findByTopicIdOrderByOrderIndexAsc(topicId));
    }

    @PutMapping("/interview-problems/{id}")
    public ResponseEntity<InterviewProblem> updateProblem(@PathVariable Long id, @RequestBody InterviewProblem problem) {
        problem.setId(id);
        return ResponseEntity.ok(problemRepository.save(problem));
    }

    @DeleteMapping("/interview-problems/{id}")
    public ResponseEntity<String> deleteProblem(@PathVariable Long id) {
        problemRepository.deleteById(id);
        return ResponseEntity.ok("Problem deleted");
    }
}