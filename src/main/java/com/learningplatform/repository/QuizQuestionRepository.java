package com.learningplatform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.learningplatform.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    // ✅ module.id — Spring Data handle karega
    List<QuizQuestion> findByModuleId(Long moduleId);
    
 // QuizQuestionRepository.java
}