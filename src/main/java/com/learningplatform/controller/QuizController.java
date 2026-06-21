package com.learningplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learningplatform.dto.QuizRequest;
import com.learningplatform.entity.Quiz;
import com.learningplatform.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create/{moduleId}")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizRequest request, @PathVariable Long moduleId) {
        return ResponseEntity.ok(quizService.createQuiz(request, moduleId));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Quiz>> getQuizByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(quizService.getQuizByModule(moduleId));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    // ✅ DELETE endpoint – sahi path
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuizById(id);
            return ResponseEntity.ok().body("{\"message\": \"Quiz deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}