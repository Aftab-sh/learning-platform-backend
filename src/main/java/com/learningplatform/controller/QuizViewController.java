package com.learningplatform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.learningplatform.dto.QuizRequest;
import com.learningplatform.entity.Quiz;
import com.learningplatform.service.QuizService;



@Controller
@RequestMapping("/teacher/quiz")
public class QuizViewController {

    @Autowired
    private QuizService quizService;

    // ================= CREATE QUIZ PAGE =================

    @GetMapping("/create")
    public String createQuizPage(
            @RequestParam Long moduleId,
            Model model){

        model.addAttribute("moduleId", moduleId);

        return "create-quiz";
    }

    // ================= SAVE QUIZ =================

    @PostMapping("/create")
    public String createQuiz(
            @ModelAttribute QuizRequest request,
            @RequestParam Long moduleId){

        Quiz quiz =
                quizService.createQuiz(
                        request,
                        moduleId);

        return "redirect:/teacher/question/create?quizId="
                + quiz.getId();
    }
    
    
    
    @GetMapping("/list")
    public String quizList(
            @RequestParam Long moduleId,
            Model model){

        List<Quiz> quizzes =
                quizService.getQuizByModule(moduleId);

        model.addAttribute("quizzes", quizzes);

        model.addAttribute("moduleId", moduleId);

        return "quiz-list";
    }

}
