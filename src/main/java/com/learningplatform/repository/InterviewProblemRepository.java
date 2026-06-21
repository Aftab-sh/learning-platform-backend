package com.learningplatform.repository;

import com.learningplatform.entity.InterviewProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterviewProblemRepository extends JpaRepository<InterviewProblem, Long> {
    List<InterviewProblem> findByTopicIdOrderByOrderIndexAsc(Long topicId);
    
    List<InterviewProblem> findByDifficulty(String difficulty);
    
    // ✅ Corrected method for both filters
    @Query("SELECT p FROM InterviewProblem p WHERE p.topic.id = :topicId AND p.difficulty = :difficulty")
    List<InterviewProblem> findByTopicIdAndDifficulty(@Param("topicId") Long topicId, @Param("difficulty") String difficulty);
}