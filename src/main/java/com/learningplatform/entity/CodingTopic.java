package com.learningplatform.entity;




import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Coding_topic")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodingTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;  // e.g., "Arrays", "Strings", "Recursion", "Trees"
    
    private String description;
    
    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)

    private List<InterviewProblem> problems;
}
