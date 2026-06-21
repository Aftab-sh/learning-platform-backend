package com.learningplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.entity.QuizQuestion;
import com.learningplatform.repository.ModuleRepository;
import com.learningplatform.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class QuestionRestController {

    
    private final QuizQuestionRepository quizQuestionRepository;

    
    private final ModuleRepository moduleRepository;  // ✅ need to fetch module
    
    @Autowired
    QuestionRestController(ModuleRepository moduleRepository,QuizQuestionRepository quizQuestionRepository)
    {
    	this.moduleRepository=moduleRepository;
    	this.quizQuestionRepository=quizQuestionRepository;
    }

    @PostMapping("/quiz-questions")
    public ResponseEntity<QuizQuestion> createQuizQuestion(
            @RequestParam Long moduleId,
            @RequestParam String questionText,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam int correctOption,
            @RequestParam int marks) {

        // Fetch module entity
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // Convert options to JSON
        List<String> options = Arrays.asList(optionA, optionB, optionC, optionD);
        String optionsJson;
        try {
            ObjectMapper mapper = new ObjectMapper();
            optionsJson = mapper.writeValueAsString(options);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize options");
        }

        QuizQuestion qq = new QuizQuestion();
        qq.setModule(module);          // ✅ set module object
        qq.setQuestionText(questionText);
        qq.setOptions(optionsJson);
        qq.setCorrectOption(correctOption);
        qq.setMarks(marks);

        return ResponseEntity.ok(quizQuestionRepository.save(qq));
    }

    @GetMapping("/quiz-questions")
    public ResponseEntity<List<QuizQuestion>> getQuizQuestionsByModule(@RequestParam Long moduleId) {
        // ✅ Repository method must be: List<QuizQuestion> findByModule_Id(Long moduleId)
        return ResponseEntity.ok(quizQuestionRepository.findByModuleId(moduleId));
    }

    @DeleteMapping("/quiz-questions/{questionId}")
    public ResponseEntity<String> deleteQuizQuestion(@PathVariable Long questionId) {
        quizQuestionRepository.deleteById(questionId);
        return ResponseEntity.ok("Deleted");
    }
}

//
//
//import com.learningplatform.dto.QuestionRequest;
//import com.learningplatform.entity.Question;
//import com.learningplatform.entity.Quiz;
//import com.learningplatform.entity.QuizQuestion;
//import com.learningplatform.repository.QuizQuestionRepository;
//import com.learningplatform.service.QuestionService1;
//import com.learningplatform.service.QuizService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//
//
//@RestController
//@RequestMapping("/api/teacher")
//@PreAuthorize("hasRole('TEACHER')")
//public class QuestionRestController
//
//{
//
//    @Autowired
//    private QuestionService1 questionService;
//
//    @Autowired
//    private QuizService quizService;
//    
//    @Autowired
//    private QuizQuestionRepository quizQuestionRepository;
//
//
//   
//
////    // 🔥 DELETE QUESTION
////    @DeleteMapping("/quiz-questions/{questionId}")
////    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {
////        questionService.deleteQuestion(questionId);
////        return ResponseEntity.ok("Question deleted");
////    }
////
////    private String convertIndexToLetter(int index) {
////        switch (index) {
////            case 0: return "A";
////            case 1: return "B";
////            case 2: return "C";
////            case 3: return "D";
////            default: return "A";
////        }
////    }
//    
//  
////    
////    @PutMapping("/quiz-questions/{questionId}")
////    public ResponseEntity<Question> updateQuestion(
////            @PathVariable Long questionId,
////            @RequestParam String questionText,
////            @RequestParam String optionA,
////            @RequestParam String optionB,
////            @RequestParam String optionC,
////            @RequestParam String optionD,
////            @RequestParam int correctOption,
////            @RequestParam int marks) {
////
////        Question question = questionService.getQuestionById(questionId);
////        question.setQuestionText(questionText);
////        question.setOptionA(optionA);
////        question.setOptionB(optionB);
////        question.setOptionC(optionC);
////        question.setOptionD(optionD);
////        question.setCorrectAnswer(convertIndexToLetter(correctOption));
////        // Note: marks field may need to be added to Question entity if not exists.
////        // Assuming Question entity has marks field. If not, add it.
////        // For now, we'll assume marks is not stored – you can extend later.
////        
////        Question updated = questionService.updateQuestion(question); // you need to implement update in service
////        return ResponseEntity.ok(updated);
////    }
//    
////    @GetMapping("/quiz-questions/{questionId}")
////    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
////        Question question = questionService.getQuestionById(questionId);
////        return ResponseEntity.ok(question);
////    }
//    
//    
//    
//}
