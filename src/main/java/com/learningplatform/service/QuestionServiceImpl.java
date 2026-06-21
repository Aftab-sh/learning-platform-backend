package com.learningplatform.service;

import com.learningplatform.Exception.ResourceNotFoundException;
import com.learningplatform.dto.QuestionRequest;
import com.learningplatform.entity.Question;
import com.learningplatform.entity.Quiz;
import com.learningplatform.repository.QuestionRepository;
import com.learningplatform.repository.QuizRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService1 {

    
    private final QuestionRepository questionRepo;

    
    private final QuizRepository quizRepo;

    
    private final ModelMapper modelMapper;
    
    @Autowired
    QuestionServiceImpl(ModelMapper modelMapper,QuizRepository quizRepo,QuestionRepository questionRepo)
    {
    	this.modelMapper=modelMapper;
    	this.quizRepo=quizRepo;
    	this.questionRepo=questionRepo;
    	
    }
    

    @Override
    public Question createQuestion(QuestionRequest request, Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        Question question = modelMapper.map(request, Question.class);
        question.setQuiz(quiz);

        return questionRepo.save(question);
    }

    // ✅ Only one implementation of getQuestionsByQuiz
    @Override
    public List<Question> getQuestionsByQuiz(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
        return questionRepo.findByQuiz(quiz);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
    }

    // ✅ Add delete method
    @Override
    public void deleteQuestion(Long questionId) {
        Question question = getQuestionById(questionId);
        questionRepo.delete(question);
    }
    
    @Override
    public Question updateQuestion(Question questionId) {
        return questionRepo.save(questionId);
    }
}