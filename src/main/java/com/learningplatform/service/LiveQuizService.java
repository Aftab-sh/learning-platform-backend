package com.learningplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LiveQuizService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public static class LiveQuestion {
        public Long id;
        public String questionText;
        public List<String> options;
        public int correctOption;
        public LiveQuestion(Long id, String text, List<String> opts, int correct) {
            this.id = id; this.questionText = text; this.options = opts; this.correctOption = correct;
        }
    }

    public static class QuizRoom 
    {
        public String roomCode;
        public String title;
        public List<LiveQuestion> questions = new ArrayList<>();
        public int currentQuestionIndex = -1;
        public boolean active = false;
        public Map<String, Integer> scores = new ConcurrentHashMap<>();
        public Map<String, Integer> currentQuestionResponseTime = new ConcurrentHashMap<>();
        public Set<String> students = ConcurrentHashMap.newKeySet();
        private long nextId = 1;

        public Long addQuestion(String text, List<String> opts, int correct) {
            Long id = nextId++;
            questions.add(new LiveQuestion(id, text, opts, correct));
            return id;
        }
        public void removeQuestion(Long id) { questions.removeIf(q -> q.id.equals(id)); }
    }

    private final Map<String, QuizRoom> rooms = new ConcurrentHashMap<>();

    public QuizRoom createRoom(String title) {
        String roomCode = generateRoomCode();
        QuizRoom room = new QuizRoom();
        room.roomCode = roomCode;
        room.title = title;
        rooms.put(roomCode, room);
        return room;
    }

    public QuizRoom getRoom(String roomCode) { return rooms.get(roomCode); 
    }

    
    public List<LiveQuestion> getRoomQuestions(String roomCode) {
        QuizRoom room = rooms.get(roomCode);
        return room == null ? Collections.emptyList() : new ArrayList<>(room.questions);
    }

 
    
    public boolean joinRoom(String roomCode, String studentName) {
        QuizRoom room = rooms.get(roomCode);

        if (room == null) {
            return false;
        }

        room.students.add(studentName);
        room.scores.putIfAbsent(studentName, 0);

        broadcastParticipants(roomCode);
        broadcastLeaderboard(roomCode);

        return true;
    }

    public boolean startQuiz(String roomCode) {
        QuizRoom room = rooms.get(roomCode);
        if (room == null || room.questions.isEmpty()) return false;
        room.active = true;
        room.currentQuestionIndex = 0;
        room.currentQuestionResponseTime.clear();
        // Reset scores? Usually keep existing scores. If you want fresh start, uncomment:
        // for (String s : room.students) room.scores.put(s, 0);
        broadcastLeaderboard(roomCode);
        return true;
    }

    public LiveQuestion getCurrentQuestion(String roomCode) {
        QuizRoom room = rooms.get(roomCode);
        if (room == null || room.currentQuestionIndex < 0 || room.currentQuestionIndex >= room.questions.size()) return null;
        return room.questions.get(room.currentQuestionIndex);
    }

    // ✅ SUBMIT ANSWER with broadcast
    public int submitAnswerAndBroadcast(String roomCode,
            String studentName,
            Long questionId,
            int selectedOption,
            int remainingPoints) {

QuizRoom room = rooms.get(roomCode);

if (room == null || !room.active) {
return -1;
}

LiveQuestion q = getCurrentQuestion(roomCode);

if (q == null) {
return -1;
}

if (!q.id.equals(questionId)) {
return -1;
}

// already answered
if (room.currentQuestionResponseTime.containsKey(studentName)) {
return -1;
}

boolean correct = selectedOption == q.correctOption;

int earnedPoints = correct ? remainingPoints : 0;

room.currentQuestionResponseTime.put(studentName, earnedPoints);

int currentScore = room.scores.getOrDefault(studentName, 0);

room.scores.put(studentName, currentScore + earnedPoints);

// LIVE UPDATE
//broadcastLeaderboard(roomCode);
broadcastCurrentQuestionLeaderboard(roomCode);

return earnedPoints;
}
    public boolean nextQuestion(String roomCode) {

        QuizRoom room = rooms.get(roomCode);

        if (room == null) {
            return false;
        }

        room.currentQuestionIndex++;

        room.currentQuestionResponseTime.clear();
        broadcastCurrentQuestionLeaderboard(roomCode);

        // QUIZ END
        if (room.currentQuestionIndex >= room.questions.size()) 
        {

            room.active = false;

            messagingTemplate.convertAndSend(
                    "/topic/quiz/" + roomCode + "/question",
                    Map.of("ended", true)
            );

            broadcastLeaderboard(roomCode);

            return true;
        }

        // SEND NEXT QUESTION
        LiveQuestion nextQuestion = room.questions.get(room.currentQuestionIndex);

        messagingTemplate.convertAndSend(
                "/topic/quiz/" + roomCode + "/question",
                nextQuestion
        );

        broadcastLeaderboard(roomCode);

        return false;
    }
    public Map<String, Integer> getLeaderboard(String roomCode) {
        QuizRoom room = rooms.get(roomCode);
        if (room == null) return Collections.emptyMap();
        return room.scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

    public List<String> getParticipants(String roomCode) {
        QuizRoom room = rooms.get(roomCode);
        return room == null ? Collections.emptyList() : new ArrayList<>(room.students);
    }

    public void addQuestionToRoom(String roomCode, String text, List<String> options, int correctOption) {
        QuizRoom room = rooms.get(roomCode);
        if (room != null) room.addQuestion(text, options, correctOption);
    }

    public void removeQuestionFromRoom(String roomCode, Long questionId) {
        QuizRoom room = rooms.get(roomCode);
        if (room != null) room.removeQuestion(questionId);
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Helper broadcast methods
    private void broadcastLeaderboard(String roomCode) {
        messagingTemplate.convertAndSend("/topic/quiz/" + roomCode + "/leaderboard", getLeaderboard(roomCode));
    }

    private void broadcastParticipants(String roomCode) {
        messagingTemplate.convertAndSend("/topic/quiz/" + roomCode + "/participants", getParticipants(roomCode));
    }
    
    public Map<String, Integer> getCurrentQuestionLeaderboard(String roomCode) {

        QuizRoom room = rooms.get(roomCode);

        if (room == null) {
            return Collections.emptyMap();
        }

        return room.currentQuestionResponseTime.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(
                        LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        Map::putAll
                );
    }
    
    private void broadcastCurrentQuestionLeaderboard(String roomCode) {

        messagingTemplate.convertAndSend(
                "/topic/quiz/" + roomCode + "/leaderboard",
                getCurrentQuestionLeaderboard(roomCode)
        );
    }
}