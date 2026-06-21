package com.learningplatform.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ModuleProgressDTO {
    private Long moduleId;
    private String moduleTitle;
    private int orderIndex;
    private boolean completed;
    private int quizScore;
    private LocalDateTime completedAt;
}