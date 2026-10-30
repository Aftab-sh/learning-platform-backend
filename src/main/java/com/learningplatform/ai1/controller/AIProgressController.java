

package com.learningplatform.ai1.controller;

import com.learningplatform.ai1.dto.StudentProgressSummaryDTO;
import com.learningplatform.ai1.service.AIService;
import com.learningplatform.ai1.service.LearningProgressService;
import com.learningplatform.ai1.service.LearningPromptService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/progress")
public class AIProgressController {

    private final LearningProgressService learningProgressService;
    private final LearningPromptService learningPromptService;
    private final AIService aiService;

    public AIProgressController(
            LearningProgressService learningProgressService,
            LearningPromptService learningPromptService,
            AIService aiService) {

        this.learningProgressService = learningProgressService;
        this.learningPromptService = learningPromptService;
        this.aiService = aiService;
    }

    @GetMapping("/{courseId}/learning-plan")
    public String generateLearningPlan(
            @PathVariable Long courseId,
            Authentication authentication) {

        String email = authentication.getName();

        Long studentId =
                learningProgressService.getStudentIdByEmail(email);

        StudentProgressSummaryDTO summary =
                learningProgressService.createProgressSummary(
                        email, studentId, courseId);

        String prompt =
                learningPromptService.createLearningPrompt(summary);

        return aiService.generateLearningPlan(prompt);
    }
}
