//package com.learningplatform.repository;
//
//import com.learningplatform.entity.CodingProblem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//
//@Repository
//public interface CodingProblemRepository 
//    extends JpaRepository<CodingProblem, Long> {
//    
//    List<CodingProblem> findByModuleIdOrderByOrderIndexAsc(
// 
//    	Long moduleId);
//    
//    List<CodingProblem> findByDifficultyIgnoreCase(String difficulty);
//   
//}

package com.learningplatform.repository;

import com.learningplatform.entity.CodingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long> {

    // ✅ moduleId → module.id
    List<CodingProblem> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    List<CodingProblem> findByDifficultyIgnoreCase(String difficulty);
}