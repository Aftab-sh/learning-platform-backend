package com.learningplatform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learningplatform.dto.QuestionRequest;
import com.learningplatform.entity.Question;
import com.learningplatform.service.QuestionService1;


@Controller
	 @RequestMapping("/teacher/question")
	    public class QuestionViewController 
	    {
	
	private final QuestionService1 questionService;
	
	
	@Autowired
	QuestionViewController(QuestionService1 questionService)
	{
		this.questionService=questionService;
		
	}

	        @GetMapping("/create")
	        public String createQuestionPage(
	                @RequestParam Long quizId,
	                Model model){

	            model.addAttribute("quizId", quizId);

	            return "create-question";
	        }
	    
	    @PostMapping("/create")
	    public String createQuestion(
	            @ModelAttribute QuestionRequest request,
	            @RequestParam Long quizId){

	        questionService.createQuestion(
	                request,
	                quizId);

	        return "redirect:/teacher/question/create?quizId="
	                + quizId;
	    }
	    
	    @GetMapping("/list")
	    public String questionList(
	            @RequestParam Long quizId,
	            Model model){

	        List<Question> questions =
	                questionService.getQuestionsByQuiz(
	                        quizId);

	        model.addAttribute(
	                "questions",
	                questions);

	        model.addAttribute(
	                "quizId",
	                quizId);

	        return "question-list";
	    }
	    
	    @GetMapping("/view")
	    public String viewQuestion(
	            @RequestParam Long questionId,
	            Model model){

	        Question question =
	                questionService.getQuestionById(
	                        questionId);

	        model.addAttribute(
	                "question",
	                question);

	        return "view-question";
	    }
}