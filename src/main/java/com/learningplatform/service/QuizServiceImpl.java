package com.learningplatform.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learningplatform.Exception.ResourceNotFoundException;
import com.learningplatform.dto.QuizRequest;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.entity.Quiz;
import com.learningplatform.repository.ModuleRepository;
import com.learningplatform.repository.QuizRepository;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepo;
    private final ModuleRepository moduleRepo;
    private final ModelMapper modelMapper;

    @Autowired
    QuizServiceImpl(QuizRepository quizRepo, ModuleRepository moduleRepo, ModelMapper modelMapper) {
        this.quizRepo = quizRepo;
        this.moduleRepo = moduleRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public Quiz createQuiz(QuizRequest request, Long moduleId) {
        ModuleEntity module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

        Quiz quiz = modelMapper.map(request, Quiz.class);
        quiz.setModule(module);

        return quizRepo.save(quiz);
    }

    @Override
    public List<Quiz> getQuizByModule(Long moduleId) {
        ModuleEntity module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));
        return quizRepo.findByModule(module);
    }

    @Override
    public Quiz getQuizById(Long quizId) {
        return quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
    }

    @Override
    public void deleteQuizById(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
        quizRepo.delete(quiz);
    }
}