//package com.learningplatform.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//public class QuizQuestion {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long moduleId;
//
//    @Column(columnDefinition = "TEXT")
//    private String questionText;
//
//    // Store options as JSON string or use List
//    private String options;   // "[\"opt1\",\"opt2\",\"opt3\",\"opt4\"]" – you can convert later
//
//    private Integer correctOption;  // 0-3 index
//    private Integer marks = 1;
//}

package com.learningplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many quiz questions → One module
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity module;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String options;
    private Integer correctOption;
    private Integer marks = 1;
}