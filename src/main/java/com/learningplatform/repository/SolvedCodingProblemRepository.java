//package com.learningplatform.repository;
//
//import com.learningplatform.entity.SolvedCodingProblem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//import java.util.Optional;
//
//public interface SolvedCodingProblemRepository extends JpaRepository<SolvedCodingProblem, Long> {
//    List<SolvedCodingProblem> findByStudentId(Long studentId);
//    boolean existsByStudentIdAndProblemId(Long studentId, Long problemId);
//    void deleteByStudentIdAndProblemId(Long studentId, Long problemId);
//    // ✅ Yeh add karo — solved code fetch karne ke liye
//    Optional<SolvedCodingProblem> findByStudentIdAndProblemId(
//        Long studentId, Long problemId);
//}

package com.learningplatform.repository;

import com.learningplatform.entity.SolvedCodingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SolvedCodingProblemRepository extends JpaRepository<SolvedCodingProblem, Long> {

    // ✅ student.id → studentId Spring resolves automatically
    List<SolvedCodingProblem> findByStudentId(Long studentId);

    boolean existsByStudentIdAndProblemId(Long studentId, Long problemId);

    void deleteByStudentIdAndProblemId(Long studentId, Long problemId);

    Optional<SolvedCodingProblem> findByStudentIdAndProblemId(Long studentId, Long problemId);
}