package com.learningplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class StudentProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long moduleId;
    private Integer quizScore = 0;         // percentage
    private Boolean moduleCompleted = false;
    private LocalDateTime completedAt;
}