package com.learningplatform.controller;

import com.learningplatform.service.LiveQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class LiveQuizStompController {

    private final LiveQuizService quizService;

    
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    LiveQuizStompController(SimpMessagingTemplate messagingTemplate,LiveQuizService quizService)
    {
    	this.messagingTemplate=messagingTemplate;
    	this.quizService=quizService;
    }

    @MessageMapping("/quiz.join")
    public void joinQuiz(@Payload Map<String, String> payload) {
        String roomCode = payload.get("roomCode");
        String studentName = payload.get("studentName");
        boolean joined = quizService.joinRoom(roomCode, studentName);
        if (joined) {
            // Send current question if quiz already active
            var question = quizService.getCurrentQuestion(roomCode);
            if (question != null) {
                messagingTemplate.convertAndSend("/topic/quiz/" + roomCode + "/question", question);
            }
        }
    }

    @MessageMapping("/quiz.answer")
    public void submitAnswer(@Payload Map<String, Object> payload) {
        String roomCode = (String) payload.get("roomCode");
        Long questionId = Long.valueOf(payload.get("questionId").toString());
        int selectedOption = (int) payload.get("selectedOption");
        String studentName = (String) payload.get("studentName");
        int remainingPoints = (int) payload.get("remainingPoints");

        quizService.submitAnswerAndBroadcast(roomCode, studentName, questionId, selectedOption, remainingPoints);
    }
}