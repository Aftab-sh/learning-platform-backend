
package com.learningplatform.ai1.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VectorStorePersistenceService {

    private final SimpleVectorStore vectorStore;
    private final File storageFile;

    public VectorStorePersistenceService(
            SimpleVectorStore vectorStore,
            @Value("${rag.vector-store.path}") String filePath) {

        this.vectorStore = vectorStore;
        this.storageFile = new File(filePath);
    }

    @PostConstruct
    public void loadVectorStore() {

        if (storageFile.exists()) {
            vectorStore.load(storageFile);
            System.out.println("VectorStore loaded successfully");
        } else {
            System.out.println("No existing VectorStore found");
        }
    }

    public void saveVectorStore() {

        try {
            File parent = storageFile.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            vectorStore.save(storageFile);

            System.out.println("VectorStore saved successfully");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to save VectorStore", e);
        }
    }

    @PreDestroy
    public void onShutdown() {
        saveVectorStore();
    }
}
