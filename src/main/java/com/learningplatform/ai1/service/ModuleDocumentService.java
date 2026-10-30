
package com.learningplatform.ai1.service;

import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.repository.ModuleRepository;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ModuleDocumentService {

    private final ModuleRepository moduleRepository;

    private final TokenTextSplitter textSplitter =
            new TokenTextSplitter();

    public ModuleDocumentService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Document> loadCourseDocuments(Long courseId) {

        // Step 1: Fetch all modules of the course
        List<ModuleEntity> modules =
                moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // Step 2: Convert modules into Spring AI Documents
        List<Document> documents = modules.stream()
                .filter(module -> module.getContent() != null
                        && !module.getContent().isBlank())
                .map(module -> new Document(
                        module.getContent(),
                        Map.of(
                                "courseId", courseId,
                                "moduleId", module.getId(),
                                "moduleTitle", module.getTitle()
                        )
                ))
                .toList();

        // Step 3: Split documents into smaller chunks
        return textSplitter.apply(documents);
    }
}
