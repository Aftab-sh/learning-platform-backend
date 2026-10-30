
package com.learningplatform.ai1.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RagTestService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagTestService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder) {

        this.vectorStore = vectorStore;

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }

    // Search relevant documents
    public List<Document> search(String question) {
        return vectorStore.similaritySearch(question);
    }

    // Course-specific RAG answer
    public String askQuestion(Long courseId, String question) {

        String filterExpression = "courseId == " + courseId;

        return chatClient.prompt()
                .user(question)
                .advisors(advisor -> advisor.param(
                        QuestionAnswerAdvisor.FILTER_EXPRESSION,
                        filterExpression
                ))
                .call()
                .content();
    }
}
