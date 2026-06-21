package com.learningplatform.service;


import java.util.List;

import com.learningplatform.dto.QuizRequest;
import com.learningplatform.entity.Quiz;


public interface QuizService {
	
	Quiz createQuiz(QuizRequest request,Long moduleId);
	
	
	List<Quiz> getQuizByModule(Long moduleId);

	
	Quiz getQuizById(Long quizId);


	void deleteQuizById(Long id);
}
