package com.learningplatform.repository;

import com.learningplatform.entity.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {
    Optional<StudentProgress> findByStudentIdAndModuleId(Long studentId, Long moduleId);
    List<StudentProgress> findByStudentId(Long studentId);
    boolean existsByStudentIdAndModuleIdAndModuleCompletedTrue(Long studentId, Long moduleId);

    
}