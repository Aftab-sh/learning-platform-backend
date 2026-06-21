//package com.learningplatform.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "codingproblem")
//public class CodingProblem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long moduleId;
//
//    @Column(nullable = false)
//    private String title;
//
//    // Problem statement
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    // Constraints
//    @Column(columnDefinition = "TEXT")
//    private String constraintsText;
//
//    // Sample — student ko dikhega
//    private String sampleInput;
//    private String sampleOutput;
//
//    // Hidden — sirf backend use karega
//    private String hiddenTestInput;
//    private String hiddenTestOutput;
//
//    private String externalLink;
//    private int orderIndex;
//    private String difficulty;
//    private Integer marks = 10;
//}

package com.learningplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "codingproblem")
public class CodingProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many coding problems → One module
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnore
    private ModuleEntity module;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String constraintsText;

    private String sampleInput;
    private String sampleOutput;
    private String hiddenTestInput;
    private String hiddenTestOutput;

    private String externalLink;
    private int orderIndex;
    private String difficulty;
    private Integer marks = 10;
    
    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
 @JsonIgnore
 private List<SolvedCodingProblem> solvedByStudents;
}