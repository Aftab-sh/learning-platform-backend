//package com.learningplatform.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "solved_coding_problem")
//public class SolvedCodingProblem {
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
//
//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public Long getStudentId() { return studentId; }
//    public void setStudentId(Long studentId) { this.studentId = studentId; }
//    public Long getProblemId() { return problemId; }  // ✅ ye method hona chahiye
//    public void setProblemId(Long problemId) { this.problemId = problemId; }
//    public LocalDateTime getSolvedAt() { return solvedAt; }
//    public void setSolvedAt(LocalDateTime solvedAt) { this.solvedAt = solvedAt; }
//}

package com.learningplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solved_coding_problem",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "problem_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolvedCodingProblem {

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
    private CodingProblem problem;

    private LocalDateTime solvedAt;
}