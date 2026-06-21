package com.learningplatform.repository;

import com.learningplatform.entity.CodingTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CodingTopicRepository extends JpaRepository<CodingTopic, Long> {
    List<CodingTopic> findAllByOrderByNameAsc();
}