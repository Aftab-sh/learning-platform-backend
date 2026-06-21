package com.learningplatform.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "module_entity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ModuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Many modules → One course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
 
    private CourseEntity course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private int passingPercentage = 50;

    @Transient
    private boolean locked;
    
  
    
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
 @JsonIgnore
 private List<Quiz> quizzes;


    
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
 @JsonIgnore
 private List<QuizQuestion> quizQuestions;


    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
 @JsonIgnore
 private List<CodingProblem> codingProblems;
}