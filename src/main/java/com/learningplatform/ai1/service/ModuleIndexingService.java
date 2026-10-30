
package com.learningplatform.ai1.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleIndexingService {

    private final ModuleDocumentService moduleDocumentService;
    private final VectorStore vectorStore;
    private final VectorStorePersistenceService persistenceService;

    public ModuleIndexingService(
            ModuleDocumentService moduleDocumentService,
            VectorStore vectorStore,
            VectorStorePersistenceService persistenceService) {

        this.moduleDocumentService = moduleDocumentService;
        this.vectorStore = vectorStore;
        this.persistenceService = persistenceService;
    }

    public int indexCourse(Long courseId) {

        // Step 1: Delete old chunks of this course
        String filterExpression = "courseId == " + courseId;
        vectorStore.delete(filterExpression);

        // Step 2: Load latest chunks
        List<Document> chunks =
                moduleDocumentService.loadCourseDocuments(courseId);

        // Step 3: Add latest chunks
        if (!chunks.isEmpty()) {
            vectorStore.add(chunks);
        }

        // Step 4: Save updated VectorStore to JSON
        persistenceService.saveVectorStore();

        return chunks.size();
    }
}
