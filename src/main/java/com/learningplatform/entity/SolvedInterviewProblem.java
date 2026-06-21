//package com.learningplatform.entity;
////track student solves)
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "solved_interview_problem")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class SolvedInterviewProblem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(nullable = false)
//    private Long studentId;
//    
//    @Column(nullable = false)
//    private Long problemId;
//    
//    private LocalDateTime solvedAt;
//}

package com.learningplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solved_interview_problem",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "problem_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolvedInterviewProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many solved → One student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Many solved → One problem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private InterviewProblem problem;

    private LocalDateTime solvedAt;
}