package com.learningplatform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.learningplatform.entity.CourseEntity;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByTeacherId(Long teacherId);
}