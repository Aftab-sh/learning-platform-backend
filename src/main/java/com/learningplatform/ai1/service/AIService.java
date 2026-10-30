package com.learningplatform.ai1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String askAI(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    public String generateLearningPlan(String progressPrompt) {
        return chatClient.prompt()
                .user(progressPrompt)
                .call()
                .content();
    }
}

