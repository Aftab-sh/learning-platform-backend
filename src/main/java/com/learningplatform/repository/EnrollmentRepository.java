//package com.learningplatform.repository;
//
//import com.learningplatform.entity.EnrollmentEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
//    List<EnrollmentEntity> findByStudentId(long studentId);
//    boolean existsByStudentIdAndCourseId(long studentId, long courseId);
//    
//    // Check if student is enrolled in a specific course (given moduleId)
//    // We'll find by moduleId -> get courseId
//    @Query("SELECT e FROM EnrollmentEntity e WHERE e.studentId = :studentId AND e.courseId = (SELECT m.courseId FROM ModuleEntity m WHERE m.id = :moduleId)")
//    Optional<EnrollmentEntity> findByStudentIdAndModuleId(@Param("studentId") Long studentId, @Param("moduleId") Long moduleId);
//    
//    long countByCourseId(Long courseId);
//    List<EnrollmentEntity> findByStudentId(Long studentId);
//
//}

package com.learningplatform.repository;

import com.learningplatform.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    // student.id se dhundo
    List<EnrollmentEntity> findByStudentId(long studentId);

    // ✅ @ManyToOne ke baad: student.id AND course.id
    boolean existsByStudentIdAndCourseId(long studentId, long courseId);

    long countByCourseId(Long courseId);

//    // ✅ JPQL fix — ab m.course.id use hoga
//    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId AND e.course.id = (SELECT m.course.id FROM ModuleEntity m WHERE m.id = :moduleId)")
//    Optional<EnrollmentEntity> findByStudentIdAndModuleId(
//        @Param("studentId") Long studentId,
//        
//        @Param("moduleId") Long moduleId);
    
    
 // ✅ JPQL fix — m.courseId → m.course.id aur e.studentId/courseId → e.student.id/e.course.id
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId " +
           "AND e.course.id = (SELECT m.course.id FROM ModuleEntity m WHERE m.id = :moduleId)")
    Optional<EnrollmentEntity> findByStudentIdAndModuleId(
        @Param("studentId") Long studentId,
        @Param("moduleId") Long moduleId);
}