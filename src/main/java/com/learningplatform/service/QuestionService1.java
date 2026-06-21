package com.learningplatform.service;

import com.learningplatform.dto.QuestionRequest;
import com.learningplatform.entity.Question;
import java.util.List;

public interface QuestionService1 {
    Question createQuestion(QuestionRequest request, Long quizId);
    List<Question> getQuestionsByQuiz(Long quizId);
    Question getQuestionById(Long questionId);
    void deleteQuestion(Long questionId);
    Question updateQuestion(Question question);
}