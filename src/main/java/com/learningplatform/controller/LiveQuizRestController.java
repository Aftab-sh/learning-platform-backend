package com.learningplatform.controller;

import com.learningplatform.service.LiveQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/live-quiz")
public class LiveQuizRestController {

    private final LiveQuizService quizService;

    
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    LiveQuizRestController(SimpMessagingTemplate messagingTemplate,LiveQuizService quizService)
    {
    	this.quizService=quizService;
    	this.messagingTemplate=messagingTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom(@RequestParam String title) {
        var room = quizService.createRoom(title);
        return ResponseEntity.ok(Map.of("roomCode", room.roomCode));
    }
    

    @GetMapping("/room/{roomCode}/questions")
    public ResponseEntity<Map<String, Object>> getRoomQuestions(@PathVariable String roomCode) {
        var questions = quizService.getRoomQuestions(roomCode);
        return ResponseEntity.ok(Map.of("questions", questions));
    }

    @PostMapping("/room/{roomCode}/add-question")
    public ResponseEntity<?> addQuestion(@PathVariable String roomCode,
                                         @RequestParam String questionText,
                                         @RequestParam String options,
                                         @RequestParam int correctOption) {
        List<String> opts = Arrays.asList(options.split(","));
        quizService.addQuestionToRoom(roomCode, questionText, opts, correctOption);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/room/{roomCode}/question/{questionId}")
    public ResponseEntity<?> removeQuestion(@PathVariable String roomCode,
                                            @PathVariable Long questionId) {
        quizService.removeQuestionFromRoom(roomCode, questionId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/start/{roomCode}")
    public ResponseEntity<?> startQuiz(@PathVariable String roomCode) {
        boolean started = quizService.startQuiz(roomCode);
        if (started) {
            var q = quizService.getCurrentQuestion(roomCode);
            if (q != null) {
                messagingTemplate.convertAndSend("/topic/quiz/" + roomCode + "/question", q);
            }
            return ResponseEntity.ok(Map.of("started", true, "totalQuestions", quizService.getRoomQuestions(roomCode).size()));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Cannot start quiz (no questions)"));
    }

    @PostMapping("/next/{roomCode}")
    public ResponseEntity<?> nextQuestion(@PathVariable String roomCode) {

        boolean ended = quizService.nextQuestion(roomCode);

        return ResponseEntity.ok(
        		Map.of(
                        "success", true,
                        "ended", ended
                )
        );
    }

    @GetMapping("/room/{roomCode}/question")
    public ResponseEntity<?> getCurrentQuestion(@PathVariable String roomCode) {
        var q = quizService.getCurrentQuestion(roomCode);
        if (q == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(q);
    }

    @GetMapping("/room/{roomCode}/leaderboard")
    public ResponseEntity<?> getLeaderboard(@PathVariable String roomCode) {
        return ResponseEntity.ok(quizService.getLeaderboard(roomCode));
    }

    @GetMapping("/room/{roomCode}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable String roomCode) {
        return ResponseEntity.ok(quizService.getParticipants(roomCode));
    }

    // ✅ CRITICAL: /exists endpoint – missing earlier
    @GetMapping("/room/{roomCode}/exists")
    public ResponseEntity<Map<String, Object>> roomExists(@PathVariable String roomCode) {
        var room = quizService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.status(404).body(Map.of("exists", false, "error", "Room not found"));
        }
        return ResponseEntity.ok(Map.of("exists", true, "active", room.active));
    }
}