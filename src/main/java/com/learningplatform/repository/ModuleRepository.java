//package com.learningplatform.repository;
//
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import com.learningplatform.entity.ModuleEntity;
//
//public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
//    List<ModuleEntity> findByCourseIdOrderByOrderIndexAsc(Long courseId);
//}

package com.learningplatform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.learningplatform.entity.ModuleEntity;

public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    // ✅ courseId → course.id — Spring Data automatically handle karega
    List<ModuleEntity> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}