
package com.learningplatform.ai1.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import com.learningplatform.ai1.service.EnrollmentAccessService;
import com.learningplatform.ai1.service.ModuleIndexingService;
import com.learningplatform.ai1.service.RagTestService;

@RestController
@RequestMapping("/api/rag-test")
public class RagTestController {

    private final ModuleIndexingService moduleIndexingService;
    private final RagTestService ragTestService;
    private final EnrollmentAccessService enrollmentAccessService;

    public RagTestController(
            ModuleIndexingService moduleIndexingService,
            RagTestService ragTestService,
            EnrollmentAccessService enrollmentAccessService) {

        this.moduleIndexingService = moduleIndexingService;
        this.ragTestService = ragTestService;
        this.enrollmentAccessService=enrollmentAccessService;
    }

    // Index actual course modules from MySQL
    @PostMapping("/courses/{courseId}/index")
    public String indexCourse(@PathVariable Long courseId) {

        int totalChunks =
                moduleIndexingService.indexCourse(courseId);

        return "Course indexed successfully. Total chunks: "
                + totalChunks;
    }

    // Search indexed documents
    @GetMapping("/search")
    public List<Document> search(@RequestParam String question) {

        return ragTestService.search(question);
    }
    
//    @GetMapping("/ask")
//    public String askQuestion(@RequestParam String question) {
//        return ragTestService.askQuestion(question);
//    }
    

    @GetMapping("/courses/{courseId}/ask")
    public String askQuestion(
            @PathVariable Long courseId,
            @RequestParam String question) {

        // Check whether logged-in user is enrolled
        enrollmentAccessService.validateCourseAccess(courseId);

        // Generate course-specific RAG answer
        return ragTestService.askQuestion(courseId, question);
    }


}



