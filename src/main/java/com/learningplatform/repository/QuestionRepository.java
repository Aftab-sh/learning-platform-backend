package com.learningplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learningplatform.entity.Question;
import com.learningplatform.entity.Quiz;

public interface QuestionRepository extends JpaRepository<Question,Long> {

	List<Question> findByQuiz(Quiz quiz);
	
	
}
