package com.learningplatform.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "interview_problem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coding_topic_id", nullable = false)
    @JsonIgnore
    private CodingTopic topic;   // ✅ field name is "topic", not "topicId"

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String constraintsText;
    private String sampleInput;
    private String sampleOutput;
    private String hiddenTestInput;
    private String hiddenTestOutput;
    private String difficulty;
    private Integer marks;
    private int orderIndex;
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SolvedInterviewProblem> solvedByStudents;
    
    
}