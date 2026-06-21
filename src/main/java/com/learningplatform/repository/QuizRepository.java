package com.learningplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz,Long> {

	List<Quiz> findByModule(ModuleEntity module);
	
}