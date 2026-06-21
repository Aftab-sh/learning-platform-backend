//package com.learningplatform.repository;
//
//import com.learningplatform.entity.SolvedInterviewProblem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface SolvedInterviewProblemRepository extends JpaRepository<SolvedInterviewProblem, Long> {
//    List<SolvedInterviewProblem> findByStudentId(Long studentId);
//    boolean existsByStudentIdAndProblemId(Long studentId, Long problemId);
//    long countByStudentId(Long studentId);
//
//}

package com.learningplatform.repository;

import com.learningplatform.entity.SolvedInterviewProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolvedInterviewProblemRepository extends JpaRepository<SolvedInterviewProblem, Long> {

    List<SolvedInterviewProblem> findByStudentId(Long studentId);

    boolean existsByStudentIdAndProblemId(Long studentId, Long problemId);

    long countByStudentId(Long studentId);
}